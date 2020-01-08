package ovh.roro.gamepasschallenge.thewalls.game.team;

import java.util.UUID;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class GameTeamPlayer {

    private final UUID uuid;
    private final String name;
    private int kills;
    private int position;

    public GameTeamPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUniqueId() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public int getKills() {
        return this.kills;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void incrementKills() {
        this.kills++;
    }
}
