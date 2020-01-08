package ovh.roro.gamepasschallenge.thewalls.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ovh.roro.gamepasschallenge.thewalls.game.GameManager;
import ovh.roro.gamepasschallenge.thewalls.game.GameState;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class StartCommand extends Command {

    public StartCommand() {
        super("start");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player && !sender.isOp())
            return false;

        if (GameManager.getInstance().getState() != GameState.WAITING) {
            sender.sendMessage("§cUne partie est déjà en cours !");
            return false;
        }

        GameManager.getInstance().startGame();
        return true;
    }
}
