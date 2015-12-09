package amidst.mojangapi.world.icon;

import java.util.LinkedList;
import java.util.List;

import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.MovablePlayerList;
import amidst.mojangapi.world.Player;

public class PlayerProducer extends CachedWorldIconProducer {
	private final MovablePlayerList movablePlayerList;

	public PlayerProducer(RecognisedVersion recognisedVersion,
			MovablePlayerList movablePlayerList) {
		super(recognisedVersion);
		this.movablePlayerList = movablePlayerList;
	}

	@Override
	protected List<WorldIcon> createCache() {
		List<WorldIcon> result = new LinkedList<WorldIcon>();
		for (Player player : movablePlayerList) {
			result.add(new WorldIcon(player.createCoordinates(), player
					.getPlayerName(), player.getSkin()));
		}
		return result;
	}
}
