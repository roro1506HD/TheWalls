package ovh.roro.gamepasschallenge.thewalls.game;

import net.minecraft.server.v1_8_R3.WorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import ovh.roro.gamepasschallenge.thewalls.TheWalls;
import ovh.roro.gamepasschallenge.thewalls.game.json.JsonBorder;
import ovh.roro.gamepasschallenge.thewalls.game.scoreboard.ScoreboardManager;
import ovh.roro.gamepasschallenge.thewalls.game.team.GameDoor;
import ovh.roro.gamepasschallenge.thewalls.game.team.GameTeam;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class GameLoop implements Runnable {

    private int wallsShrink;
    private boolean updateBorder;

    GameLoop() {
    }

    void reset() {
        this.wallsShrink = TheWalls.getInstance().getGameConfig().getBorder().getShrinkStart();
        this.updateBorder = false;
    }

    @Override
    public void run() {
        GameManager gameManager = GameManager.getInstance();
        int timeElapsed = gameManager.increaseTimeElapsed();

        if (!this.updateBorder && --this.wallsShrink == 0) {
            this.updateBorder = true;

            JsonBorder border = TheWalls.getInstance().getGameConfig().getBorder();

            WorldBorder worldBorder = ((CraftWorld) Bukkit.getWorld("gameworld")).getHandle().getWorldBorder();

            worldBorder.transitionSizeBetween(worldBorder.getSize(), border.getEndSize(), border.getShrinkTime() * 50);

            for (GameTeam team : gameManager.getTeamManager().getTeams())
                team.getDoors().forEach(GameDoor::destroy);

            gameManager.getAllPlayers().forEach(player -> {
                player.sendMessage("§aLes portes sont désormais ouvertes ! Dépêchez-vous, la bordure réduit !");
                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ANVIL_LAND, 100.0F, 1.0F);
            });
        } else if (!this.updateBorder && this.wallsShrink % 300 == 0)
            gameManager.getAllPlayers().forEach(player -> {
                player.sendMessage("§eLes portes s'ouvriront et la bordure commencera sa réduction dans §6§l" + (this.wallsShrink / 60) + " minutes §e!");
                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ORB_PICKUP, 100.0F, 2.0F);
            });

        if (this.updateBorder) {
            gameManager.getAllPlayers().forEach(player -> {
                ScoreboardManager.getInstance().getGameScoreboard().updateBorder(player);
                ScoreboardManager.getInstance().getGameScoreboard().updateTimer(player);
            });
        } else
            gameManager.getAllPlayers().forEach(ScoreboardManager.getInstance().getGameScoreboard()::updateTimer);
    }
}
