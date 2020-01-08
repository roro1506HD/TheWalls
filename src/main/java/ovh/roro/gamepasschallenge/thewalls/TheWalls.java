package ovh.roro.gamepasschallenge.thewalls;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import ovh.roro.gamepasschallenge.thewalls.command.SerializeItemCommand;
import ovh.roro.gamepasschallenge.thewalls.command.SpecCommand;
import ovh.roro.gamepasschallenge.thewalls.command.StartCommand;
import ovh.roro.gamepasschallenge.thewalls.game.GameManager;
import ovh.roro.gamepasschallenge.thewalls.game.json.JsonConfig;
import ovh.roro.gamepasschallenge.thewalls.game.player.GamePlayer;
import ovh.roro.gamepasschallenge.thewalls.listener.DeathListener;
import ovh.roro.gamepasschallenge.thewalls.listener.PlayerListener;
import ovh.roro.gamepasschallenge.thewalls.util.json.ItemStackTypeAdapter;
import ovh.roro.gamepasschallenge.thewalls.util.json.LocationTypeAdapter;

import java.io.File;
import java.io.FileReader;
import java.util.logging.Level;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class TheWalls extends JavaPlugin {

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Location.class, new LocationTypeAdapter())
            .registerTypeAdapter(ItemStack.class, new ItemStackTypeAdapter())
            .create();

    private static TheWalls instance;

    private JsonConfig gameConfig;

    public static TheWalls getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // Check and generate plugin folder if not already existing
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
            this.saveResource("config.json", false);
            this.setEnabled(false);
            return;
        } else {
            try (FileReader reader = new FileReader(new File(getDataFolder(), "config.json"))) {
                this.gameConfig = GSON.fromJson(reader, JsonConfig.class);
            } catch (Exception ex) {
                getLogger().log(Level.SEVERE, "Could not read config.json", ex);
                this.setEnabled(false);
                return;
            }
        }

        // Launch Game Logic
        new GameManager();


        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);

        // Register commands
        SimpleCommandMap commandMap = ((CraftServer) getServer()).getCommandMap();
        commandMap.register(getDescription().getName(), new StartCommand());
        commandMap.register(getDescription().getName(), new SpecCommand());
        commandMap.register(getDescription().getName(), new SerializeItemCommand());

        // Register online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            GamePlayer gamePlayer = GameManager.getInstance().addPlayer(player);
            gamePlayer.initialize(true);
        }

        // Check and unload/remove game world if existing
        if (Bukkit.getWorld("gameworld") != null)
            Bukkit.unloadWorld("gameworld", false);

        try {
            FileUtils.deleteDirectory(new File("gameworld"));
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "An exception occurred while deleting game world folder.");
        }

    }

    @Override
    public void onDisable() {
        GameManager.getInstance().getAllPlayers().forEach(player -> player.getScoreboard().destroy());
    }

    public JsonConfig getGameConfig() {
        return this.gameConfig;
    }
}
