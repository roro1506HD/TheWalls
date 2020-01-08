package ovh.roro.gamepasschallenge.thewalls.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ovh.roro.gamepasschallenge.thewalls.game.GameManager;
import ovh.roro.gamepasschallenge.thewalls.game.GameState;
import ovh.roro.gamepasschallenge.thewalls.game.player.GamePlayer;
import ovh.roro.gamepasschallenge.thewalls.game.scoreboard.ScoreboardManager;
import ovh.roro.gamepasschallenge.thewalls.game.scoreboard.defaults.WaitingScoreboard;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class SpecCommand extends Command {

    public SpecCommand() {
        super("spec");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is restricted to physical players.");
            return false;
        }

        if (!sender.isOp()) {
            sender.sendMessage("§cVous n'avez pas accès à cette commande.");
            return false;
        }

        GameManager gameManager = GameManager.getInstance();
        WaitingScoreboard waitingScoreboard = ScoreboardManager.getInstance().getWaitingScoreboard();

        if (gameManager.getState() != GameState.WAITING) {
            sender.sendMessage("§cVous ne pouvez pas changer votre mode de jeu durant une partie.");
            return false;
        }

        GamePlayer gamePlayer = gameManager.getPlayer((((Player) sender).getUniqueId()));

        if (gamePlayer.getTeam() != null)
            gamePlayer.getTeam().removePlayer(gamePlayer);

        if (gamePlayer.isSpectator())
            sender.sendMessage("§eVous serez compté comme joueur durant la partie.");
        else
            sender.sendMessage("§aVous serez compté comme spectateur durant la partie.");

        gamePlayer.setSpectator(!gamePlayer.isSpectator());
        gameManager.getAllPlayers().forEach(waitingScoreboard::updatePlayers);
        return true;
    }
}