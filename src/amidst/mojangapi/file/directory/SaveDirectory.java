package amidst.mojangapi.file.directory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import org.jnbt.CompoundTag;

import amidst.mojangapi.file.nbt.LevelDat;
import amidst.mojangapi.file.nbt.NBTUtils;

public class SaveDirectory {
	/**
	 * Returns a new valid instance of the class SaveDirectory. It tries to use
	 * the given file. If that is not valid it tires to use its parent file. If
	 * that is also not valid it will throw a FileNotFoundException.
	 * 
	 * @return The SaveDirectory, but never null.
	 * @throws FileNotFoundException
	 */
	public static SaveDirectory from(File file) throws FileNotFoundException {
		File currentFile = file;
		SaveDirectory result = null;
		if (currentFile == null) {
			// error
		} else {
			result = createValidSaveDirectory(currentFile);
			currentFile = currentFile.getParentFile();
			if (result != null) {
				return result;
			} else if (currentFile == null) {
				// error
			} else {
				result = createValidSaveDirectory(currentFile);
				currentFile = currentFile.getParentFile();
				if (result != null) {
					return result;
				} else {
					// error
				}
			}
		}
		throw new FileNotFoundException("unable to load save directory: "
				+ file);
	}

	private static SaveDirectory createValidSaveDirectory(File currentFile) {
		SaveDirectory result = new SaveDirectory(currentFile);
		if (result.isValid()) {
			return result;
		} else {
			return null;
		}
	}

	private final File root;
	private final File players;
	private final File playerdata;
	private final File levelDat;
	private final File backupRoot;
	private final File backupPlayers;
	private final File backupPlayerdata;

	public SaveDirectory(File root) {
		this.root = root;
		this.players = new File(root, "players");
		this.playerdata = new File(root, "playerdata");
		this.levelDat = new File(root, "level.dat");
		this.backupRoot = new File(root, "amidst_backup");
		this.backupPlayers = new File(backupRoot, "players");
		this.backupPlayerdata = new File(backupRoot, "playerdata");
	}

	public boolean isValid() {
		return root.isDirectory() && levelDat.isFile();
	}

	public File getRoot() {
		return root;
	}

	public File getPlayers() {
		return players;
	}

	// TODO: use me!
	public File getPlayerdata() {
		return playerdata;
	}

	public File getLevelDat() {
		return levelDat;
	}

	public File getPlayersFile(String playerName) {
		return new File(players, playerName + ".dat");
	}

	public File getPlayerdataFile(String playerUUID) {
		return new File(playerdata, playerUUID + ".dat");
	}

	public File getBackupLevelDat() {
		return new File(backupRoot, "level.dat" + millis());
	}

	public File getBackupPlayersFile(String playerName) {
		return new File(backupPlayers, playerName + ".dat" + millis());
	}

	public File getBackupPlayerdataFile(String playerUUID) {
		return new File(backupPlayerdata, playerUUID + ".dat" + millis());
	}

	// TODO: switch to more readable timestamp
	private String millis() {
		return "_" + System.currentTimeMillis();
	}

	public File[] getPlayersFiles() {
		File[] files = players.listFiles();
		if (files == null) {
			return new File[0];
		} else {
			return files;
		}
	}

	public File[] getPlayerdataFiles() {
		File[] files = playerdata.listFiles();
		if (files == null) {
			return new File[0];
		} else {
			return files;
		}
	}

	// TODO: adjust to playerdata
	public boolean isMultiPlayer() {
		return players.isDirectory() && getPlayersFiles().length > 0;
	}

	public CompoundTag readLevelDat() throws FileNotFoundException, IOException {
		return NBTUtils.readTagFromFile(levelDat);
	}

	public LevelDat createLevelDat() throws FileNotFoundException, IOException {
		return new LevelDat(readLevelDat());
	}

	public boolean tryBackupLevelDat() {
		File backupFile = getBackupLevelDat();
		return ensureDirectoryExists(backupRoot)
				&& tryCopy(getLevelDat(), backupFile) && backupFile.isFile();
	}

	public boolean tryBackupPlayersFile(String playerName) {
		File backupFile = getBackupPlayersFile(playerName);
		return ensureDirectoryExists(backupPlayers)
				&& tryCopy(getPlayersFile(playerName), backupFile)
				&& backupFile.isFile();
	}

	public boolean tryBackupPlayerdataFile(String playerUUID) {
		File backupFile = getBackupPlayerdataFile(playerUUID);
		return ensureDirectoryExists(backupPlayerdata)
				&& tryCopy(getPlayerdataFile(playerUUID), backupFile)
				&& backupFile.isFile();
	}

	private boolean ensureDirectoryExists(File directory) {
		if (!directory.exists()) {
			directory.mkdirs();
		}
		return directory.isDirectory();
	}

	private boolean tryCopy(File from, File to) {
		try {
			Files.copy(from.toPath(), to.toPath());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
