package ovh.roro.gamepasschallenge.thewalls.game.team;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import ovh.roro.gamepasschallenge.thewalls.game.json.JsonTeam;

import java.util.Set;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class TeamManager {

    private final ImmutableMap<Integer, GameTeam> teams;

    public TeamManager(Set<JsonTeam> teams) {
        ImmutableMap.Builder<Integer, GameTeam> builder = ImmutableMap.builder();

        for (JsonTeam team : teams)
            builder.put(team.getId(), new GameTeam(team));

        this.teams = builder.build();
    }

    public GameTeam getById(int id) {
        return this.teams.get(id);
    }

    public ImmutableCollection<GameTeam> getTeams() {
        return this.teams.values();
    }

    public ImmutableSet<GameTeam> getAliveTeams() {
        ImmutableSet.Builder<GameTeam> builder = ImmutableSet.builder();

        for (GameTeam team : this.teams.values())
            if (!team.isEliminated())
                builder.add(team);

        return builder.build();
    }
}
