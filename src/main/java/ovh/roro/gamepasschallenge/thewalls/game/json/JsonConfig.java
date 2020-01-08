package ovh.roro.gamepasschallenge.thewalls.game.json;

import java.util.Set;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public final class JsonConfig {

    private JsonGame game;
    private JsonBorder border;
    private Set<JsonTeam> teams;

    public JsonGame getGame() {
        return this.game;
    }

    public JsonBorder getBorder() {
        return this.border;
    }

    public Set<JsonTeam> getTeams() {
        return this.teams;
    }
}
