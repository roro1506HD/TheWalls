package ovh.roro.gamepasschallenge.thewalls.game.json;

import com.google.gson.annotations.SerializedName;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;

import java.util.List;
import java.util.Objects;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public final class JsonTeam {

    private int id;
    private int size;

    @SerializedName("name_masculine_singular")
    private String nameMasculineSingular;

    @SerializedName("name_masculine_plural")
    private String nameMasculinePlural;

    @SerializedName("name_feminine_singular")
    private String nameFeminineSingular;

    @SerializedName("name_feminine_plural")
    private String nameFemininePlural;

    @SerializedName("chat_color")
    private ChatColor chatColor;

    @SerializedName("dye_color")
    private DyeColor dyeColor;

    private List<JsonDoor> doors;
    private Location spawn;

    public int getId() {
        return this.id;
    }

    public int getSize() {
        return this.size;
    }

    public String getNameMasculineSingular() {
        return this.nameMasculineSingular;
    }

    public String getNameMasculinePlural() {
        return this.nameMasculinePlural;
    }

    public String getNameFeminineSingular() {
        return this.nameFeminineSingular;
    }

    public String getNameFemininePlural() {
        return this.nameFemininePlural;
    }

    public ChatColor getChatColor() {
        return this.chatColor;
    }

    public DyeColor getDyeColor() {
        return this.dyeColor;
    }

    public List<JsonDoor> getDoors() {
        return this.doors;
    }

    public Location getSpawn() {
        return this.spawn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof JsonTeam))
            return false;

        JsonTeam jsonTeam = (JsonTeam) o;
        return this.id == jsonTeam.id &&
                this.size == jsonTeam.size &&
                Objects.equals(this.nameMasculineSingular, jsonTeam.nameMasculineSingular) &&
                Objects.equals(this.nameMasculinePlural, jsonTeam.nameMasculinePlural) &&
                Objects.equals(this.nameFeminineSingular, jsonTeam.nameFeminineSingular) &&
                Objects.equals(this.nameFemininePlural, jsonTeam.nameFemininePlural) &&
                this.chatColor == jsonTeam.chatColor &&
                this.dyeColor == jsonTeam.dyeColor &&
                Objects.equals(this.doors, jsonTeam.doors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.size, this.nameMasculineSingular, this.nameMasculinePlural, this.nameFeminineSingular, this.nameFemininePlural, this.chatColor, this.dyeColor, this.doors);
    }
}
