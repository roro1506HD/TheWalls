package ovh.roro.gamepasschallenge.thewalls.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import ovh.roro.gamepasschallenge.thewalls.TheWalls;
import ovh.roro.gamepasschallenge.thewalls.game.GameManager;
import ovh.roro.gamepasschallenge.thewalls.game.player.GamePlayer;
import ovh.roro.gamepasschallenge.thewalls.game.scoreboard.ScoreboardManager;
import ovh.roro.gamepasschallenge.thewalls.game.team.GameTeam;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class DeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        GameManager gameManager = GameManager.getInstance();
        GamePlayer gamePlayer = gameManager.getPlayer(player.getUniqueId());
        GameTeam gameTeam = gamePlayer.getTeam();

        event.setDeathMessage(null);
        gamePlayer.setAlive(false);
        gamePlayer.setPosition(gameManager.getAlivePlayers().size() + 1);

        TextComponent parent = new TextComponent("");
        parent.setColor(ChatColor.GRAY);

        TextComponent deathMessage = new TextComponent(player.getName());
        deathMessage.setColor(ChatColor.RED);
        deathMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Cliquez pour vous téléporter à §c" + player.getName()).create()));
        deathMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + player.getName()));

        parent.addExtra(deathMessage);

        if (player.getKiller() != null && player.getKiller() != player) {
            TextComponent partTwo = new TextComponent(" a été tué par ");
            partTwo.setColor(ChatColor.GRAY);
            partTwo.setHoverEvent(null);
            partTwo.setClickEvent(null);
            parent.addExtra(partTwo);

            TextComponent partThree = new TextComponent(player.getKiller().getName());
            partThree.setColor(ChatColor.GREEN);
            partThree.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Cliquez pour vous téléporter à §a" + player.getKiller().getName()).create()));
            partThree.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + player.getKiller().getName()));
            parent.addExtra(partThree);

            GamePlayer gameKiller = gameManager.getPlayer(player.getKiller().getUniqueId());

            if (!gameKiller.getTeam().equals(gameTeam)) {
                gameKiller.incrementKills();
                gameKiller.getTeam().incrementKills();
                ScoreboardManager.getInstance().getGameScoreboard().updateKills(gameKiller);
            }
        } else
            parent.addExtra(new TextComponent(" est mort."));

        for (Player tempPlayer : Bukkit.getOnlinePlayers()) {
            GamePlayer tempGamePlayer = gameManager.getPlayer(tempPlayer.getUniqueId());
            if (tempGamePlayer.isSpectator())
                tempPlayer.spigot().sendMessage(parent);
            else
                tempPlayer.sendMessage(parent.toLegacyText());
            ScoreboardManager.getInstance().getGameScoreboard().updatePlayers(tempGamePlayer);
        }

        gamePlayer.initialize(false);
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(TheWalls.getInstance().getGameConfig().getGame().getLobby());

        System.out.println("[DEBUG] " + gamePlayer.getName() + " eliminated offline (" + gamePlayer.getKills() + " kills, #" + gamePlayer.getTeamPlayer().getPosition() + ")");

        if (gameTeam.isEliminated()) {
            gameTeam.setPosition(gameManager.getTeamManager().getAliveTeams().size() + 1);
            gameManager.broadcastMessage("§cL'équipe " + gameTeam.getJsonTeam().getChatColor() + gameTeam.getJsonTeam().getNameFeminineSingular() + " §cest éliminée ! Elle termine #" + gameTeam.getPosition() + " !");
            System.out.println("[DEBUG] \"" + gameTeam.getJsonTeam().getNameMasculineSingular() + "\" eliminated (" + gameTeam.getTotalKills() + " kills, #" + gameTeam.getPosition() + ")");
        }

        gameManager.checkWin();
    }
}
