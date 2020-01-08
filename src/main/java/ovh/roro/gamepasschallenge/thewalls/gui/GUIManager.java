package ovh.roro.gamepasschallenge.thewalls.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import ovh.roro.gamepasschallenge.thewalls.TheWalls;
import ovh.roro.gamepasschallenge.thewalls.listener.InventoryListener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class GUIManager {

    private static final GUIManager INSTANCE = new GUIManager();

    private final Map<UUID, AbstractGUI> currentGuis;

    private GUIManager() {
        this.currentGuis = new ConcurrentHashMap<>();
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), TheWalls.getInstance());
    }

    public static GUIManager getInstance() {
        return INSTANCE;
    }

    public void openGUI(Player player, AbstractGUI gui) {
        if (this.currentGuis.containsKey(player.getUniqueId()))
            this.closeGUI(player);

        this.currentGuis.put(player.getUniqueId(), gui);
        gui.display(player);
    }

    public void closeGUI(Player player) {
        player.closeInventory();
        this.removeClosedGUI(player);
    }

    public void removeClosedGUI(Player player) {
        AbstractGUI gui = this.currentGuis.remove(player.getUniqueId());

        if (gui != null)
            gui.onClose( player);
    }

    public AbstractGUI getPlayerGUI(HumanEntity player) {
        return this.currentGuis.get(player.getUniqueId());
    }

    public Map<UUID, AbstractGUI> getCurrentGUIs() {
        return this.currentGuis;
    }
}
