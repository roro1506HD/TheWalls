package ovh.roro.gamepasschallenge.thewalls.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.PlayerInventory;
import ovh.roro.gamepasschallenge.thewalls.game.GameManager;
import ovh.roro.gamepasschallenge.thewalls.game.GameState;
import ovh.roro.gamepasschallenge.thewalls.gui.AbstractGUI;
import ovh.roro.gamepasschallenge.thewalls.gui.GUIManager;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof PlayerInventory)
            if (GameManager.getInstance().getState() == GameState.WAITING) {
                event.setCancelled(true);
                return;
            }

        AbstractGUI gui = GUIManager.getInstance().getPlayerGUI(event.getWhoClicked());

        if (gui != null) {
            String action = gui.getAction(event.getSlot());

            if (action != null)
                gui.onClick((Player) event.getWhoClicked(), event.getCurrentItem(), action, event.getSlot(), event.getClick());

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (GUIManager.getInstance().getPlayerGUI(event.getPlayer()) != null)
            GUIManager.getInstance().removeClosedGUI((Player) event.getPlayer());
    }

}
