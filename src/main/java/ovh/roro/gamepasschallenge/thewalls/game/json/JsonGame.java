package ovh.roro.gamepasschallenge.thewalls.game.json;

import com.google.gson.annotations.SerializedName;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class JsonGame {

    private Location lobby;

    @SerializedName("spec_spawn")
    private Location specSpawn;

    private List<ItemStack> starter;

    @SerializedName("unbreakable_blocks")
    private List<Material> unbreakableBlocks;

    @SerializedName("csv_script")
    private String csvScript;

    private List<String> specs;

    public Location getLobby() {
        return this.lobby;
    }

    public Location getSpecSpawn() {
        return this.specSpawn;
    }

    public List<ItemStack> getStarter() {
        return this.starter;
    }

    public List<Material> getUnbreakableBlocks() {
        return this.unbreakableBlocks;
    }

    public String getCsvScript() {
        return this.csvScript;
    }

    public List<String> getSpecs() {
        return this.specs;
    }
}
