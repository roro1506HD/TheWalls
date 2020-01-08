package ovh.roro.gamepasschallenge.thewalls.game.team;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Team;
import ovh.roro.gamepasschallenge.thewalls.TheWalls;
import ovh.roro.gamepasschallenge.thewalls.game.GameManager;
import ovh.roro.gamepasschallenge.thewalls.game.json.JsonDoor;
import ovh.roro.gamepasschallenge.thewalls.game.json.JsonTeam;
import ovh.roro.gamepasschallenge.thewalls.game.player.GamePlayer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class GameTeam {

    private final JsonTeam jsonTeam;
    private final Set<UUID> players;
    private final Set<GameDoor> doors;
    private final Set<GameTeamPlayer> playersInstances;
    private final Team scoreboardTeam;
    private String playersCsv;
    private int position;
    private int totalKills;

    GameTeam(JsonTeam jsonTeam) {
        this.jsonTeam = jsonTeam;
        this.players = new HashSet<>();
        this.playersInstances = new HashSet<>();

        ImmutableSet.Builder<GameDoor> doors = ImmutableSet.builder();

        for (JsonDoor door : jsonTeam.getDoors())
            doors.add(new GameDoor(door));

        this.doors = doors.build();

        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("team_" + jsonTeam.getId());

        if (team == null)
            team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("team_" + jsonTeam.getId());
        else
            team.getEntries().forEach(team::removeEntry);

        team.setPrefix(jsonTeam.getChatColor() + "[" + jsonTeam.getNameMasculineSingular() + "] ");

        this.scoreboardTeam = team;
    }

    public void addPlayer(GamePlayer player) {
        if (this.players.size() == this.jsonTeam.getSize())
            return;

        if (!this.players.add(player.getUniqueId()))
            return;

        if (player.getTeam() != null)
            player.getTeam().removePlayer(player);

        player.setTeam(this);
        this.scoreboardTeam.addEntry(player.getName());
    }

    public boolean removePlayer(GamePlayer player) {
        if (this.players.remove(player.getUniqueId())) {
            player.setTeam(null);
            this.scoreboardTeam.removeEntry(player.getName());
            return true;
        }

        return false;
    }

    public void initialize() {
        GameManager gameManager = GameManager.getInstance();
        this.playersInstances.clear();
        this.playersCsv = "";

        for (UUID uuid : this.players) {
            GamePlayer gamePlayer = gameManager.getPlayer(uuid);
            GameTeamPlayer teamPlayer = new GameTeamPlayer(gamePlayer.getUniqueId(), gamePlayer.getName());

            gamePlayer.setTeamPlayer(teamPlayer);
            this.playersInstances.add(teamPlayer);
            this.playersCsv += (this.playersCsv.length() == 0 ? "" : ";") + gamePlayer.getName();
        }

        this.position = 0;
        this.totalKills = 0;
    }

    public void incrementKills() {
        this.totalKills++;
    }

    public int getTotalKills() {
        return this.totalKills;
    }

    public boolean isEliminated() {
        return this.players.isEmpty() || this.players.stream()
                .map(GameManager.getInstance()::getPlayer)
                .noneMatch(GamePlayer::isAlive);
    }

    public boolean hasPlayer(UUID player) {
        return this.players.contains(player);
    }

    public JsonTeam getJsonTeam() {
        return this.jsonTeam;
    }

    public Set<UUID> getPlayers() {
        return Collections.unmodifiableSet(this.players);
    }

    public Set<GameTeamPlayer> getEntries() {
        return this.playersInstances;
    }

    public Set<GameDoor> getDoors() {
        return this.doors;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof GameTeam))
            return false;

        GameTeam gameTeam = (GameTeam) o;
        return this.jsonTeam.equals(gameTeam.jsonTeam) &&
                this.players.equals(gameTeam.players);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.jsonTeam, this.players);
    }
}
