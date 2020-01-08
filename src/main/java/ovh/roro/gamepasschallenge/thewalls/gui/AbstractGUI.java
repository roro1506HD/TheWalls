package ovh.roro.gamepasschallenge.thewalls.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public abstract class AbstractGUI {

    private final Map<Integer, String> actions;

    protected Inventory inventory;

    protected AbstractGUI() {
        this.actions = new HashMap<>();
    }

    public abstract void display(Player player);

    public void update(Player player) {
    }

    protected void update() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            AbstractGUI gui = GUIManager.getInstance().getPlayerGUI(player);
            if (gui != null && gui.getClass().isInstance(this))
                gui.update(player);
        }
    }

    public void onClose(Player player) {
    }

    public void onClick(Player player, ItemStack itemStack, String action, int slot, ClickType clickType) {
    }

    public void clearSlotData() {
        this.actions.clear();
        this.inventory.clear();
    }

    public void setSlotData(ItemStack itemStack, int slot, String action) {
        this.actions.put(slot, action);

        this.inventory.setItem(slot, itemStack);
    }

    public String getAction(int slot) {
        return this.actions.get(slot);
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}
