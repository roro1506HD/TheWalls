package ovh.roro.gamepasschallenge.thewalls.game.scoreboard.defaults;

import net.minecraft.server.v1_8_R3.EnumWorldBorderState;
import net.minecraft.server.v1_8_R3.WorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import ovh.roro.gamepasschallenge.thewalls.TheWalls;
import ovh.roro.gamepasschallenge.thewalls.game.GameManager;
import ovh.roro.gamepasschallenge.thewalls.game.player.GamePlayer;
import ovh.roro.gamepasschallenge.thewalls.game.scoreboard.IScoreboard;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class GameScoreboard implements IScoreboard {

    private int playersIndex = -1;
    private int timerIndex = -1;
    private int borderIndex = -1;
    private int killsIndex = -1;

    @Override
    public void initScoreboard(GamePlayer player) {
        int index = 0;

        player.getScoreboard().setLine(index++, "§a");

        if (this.killsIndex == -1)
            this.killsIndex = index;

        index = this.updateKills(player, index);

        player.getScoreboard().setLine(index++, "§a");

        if (this.borderIndex == -1)
            this.borderIndex = index;

        index = this.updateBorder(player, index);

        player.getScoreboard().setLine(index++, "§a");

        if (this.timerIndex == -1)
            this.timerIndex = index;

        index = this.updateTimer(player, index);

        player.getScoreboard().setLine(index++, "§a");

        if (this.playersIndex == -1)
            this.playersIndex = index;

        index = this.updatePlayers(player, index);

        player.getScoreboard().setLine(index, "§a");
    }

    public void updatePlayers(GamePlayer player) {
        this.updatePlayers(player, this.playersIndex);
    }

    private int updatePlayers(GamePlayer player, int index) {
        player.getScoreboard().setLine(index++, "Joueurs restants : §a" + GameManager.getInstance().getAlivePlayers().size());
        return index;
    }

    public void updateTimer(GamePlayer player) {
        this.updateTimer(player, this.timerIndex);
    }

    private int updateTimer(GamePlayer player, int index) {
        int time = TheWalls.getInstance().getGameConfig().getBorder().getShrinkStart() - GameManager.getInstance().getTimeElapsed();
        WorldBorder worldBorder = ((CraftWorld) Bukkit.getWorld("gameworld")).getHandle().getWorldBorder();

        player.getScoreboard().setLine(index++, "Bordure : §6" + (time <= 0 ? (worldBorder.getState() == EnumWorldBorderState.SHRINKING ? "Rétrécit..." : worldBorder.getState() == EnumWorldBorderState.GROWING ? "S'agrandit..." : "Stationnaire") : String.format("%02d:%02d", time / 60, time % 60)));
        return index;
    }

    public void updateBorder(GamePlayer player) {
        this.updateBorder(player, this.borderIndex);
    }

    private int updateBorder(GamePlayer player, int index) {
        player.getScoreboard().setLine(index++, "Taille : §e±" + String.format("%.2f", Bukkit.getWorld("gameworld").getWorldBorder().getSize() / 2.0D));
        return index;
    }

    public void updateKills(GamePlayer player) {
        this.updateKills(player, this.killsIndex);
    }

    private int updateKills(GamePlayer player, int index) {
        player.getScoreboard().setLine(index++, "Kills : §d" + player.getKills());
        return index;
    }
}
