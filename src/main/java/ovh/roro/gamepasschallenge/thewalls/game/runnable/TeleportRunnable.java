package ovh.roro.gamepasschallenge.thewalls.game.runnable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import ovh.roro.gamepasschallenge.thewalls.TheWalls;
import ovh.roro.gamepasschallenge.thewalls.game.GameManager;
import ovh.roro.gamepasschallenge.thewalls.game.player.GamePlayer;
import ovh.roro.gamepasschallenge.thewalls.game.scoreboard.ScoreboardManager;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class TeleportRunnable implements Runnable {

    private final Queue<GamePlayer> players;
    private final Runnable callback;
    private final BukkitTask task;
    private final int totalPlayers;

    public TeleportRunnable(Collection<GamePlayer> players, Runnable callback) {
        this.players = new ArrayDeque<>(players);
        this.callback = callback;
        this.totalPlayers = players.size();

        this.task = Bukkit.getScheduler().runTaskTimer(TheWalls.getInstance(), this, 4L, 4L);
    }

    @Override
    public void run() {
        if (this.players.isEmpty()) {
            this.task.cancel();
            this.callback.run();
            return;
        }

        GamePlayer player = this.players.remove();

        GameManager.getInstance().getAllPlayers().forEach(tempPlayer -> {
            tempPlayer.sendActionBar("§7Téléportation en cours... (§c" + (this.totalPlayers - this.players.size()) + "§7/§c" + this.totalPlayers + "§7)");
        });

        player.getScoreboard().clearLines();
        ScoreboardManager.getInstance().getGameScoreboard().initScoreboard(player);

        Player bukkitPlayer = player.getPlayer();

        if (!bukkitPlayer.isOnline())
            return;

        bukkitPlayer.teleport(player.getTeam().getJsonTeam().getSpawn());
        bukkitPlayer.setWalkSpeed(0.0F);
        bukkitPlayer.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 666666, 250, true, false), true);
        bukkitPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 666666, 250, true, false), true);
        bukkitPlayer.setFoodLevel(1);
    }
}
