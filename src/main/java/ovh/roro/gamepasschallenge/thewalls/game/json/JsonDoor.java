package ovh.roro.gamepasschallenge.thewalls.game.json;

import com.google.gson.annotations.SerializedName;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;
import java.util.Objects;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public final class JsonDoor {

    @SerializedName("min_corner")
    private Location minCorner;

    @SerializedName("max_corner")
    private Location maxCorner;

    @SerializedName("materials_to_destroy")
    private List<Material> materialsToDestroy;

    public void fixCorners() {
        double minX = Math.min(this.minCorner.getX(), this.maxCorner.getX());
        double minY = Math.min(this.minCorner.getY(), this.maxCorner.getY());
        double minZ = Math.min(this.minCorner.getZ(), this.maxCorner.getZ());
        float minYaw = Math.min(this.minCorner.getYaw(), this.maxCorner.getYaw());
        float minPitch = Math.min(this.minCorner.getPitch(), this.maxCorner.getPitch());

        double maxX = Math.max(this.minCorner.getX(), this.maxCorner.getX());
        double maxY = Math.max(this.minCorner.getY(), this.maxCorner.getY());
        double maxZ = Math.max(this.minCorner.getZ(), this.maxCorner.getZ());
        float maxYaw = Math.max(this.minCorner.getYaw(), this.maxCorner.getYaw());
        float maxPitch = Math.max(this.minCorner.getPitch(), this.maxCorner.getPitch());

        this.minCorner = new Location(this.minCorner.getWorld(), minX, minY, minZ, minYaw, minPitch);
        this.maxCorner = new Location(this.maxCorner.getWorld(), maxX, maxY, maxZ, maxYaw, maxPitch);
    }

    public Location getMinCorner() {
        return this.minCorner;
    }

    public Location getMaxCorner() {
        return this.maxCorner;
    }

    public List<Material> getMaterialsToDestroy() {
        return this.materialsToDestroy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof JsonDoor))
            return false;

        JsonDoor jsonDoor = (JsonDoor) o;
        return Objects.equals(this.minCorner, jsonDoor.minCorner) &&
                Objects.equals(this.maxCorner, jsonDoor.maxCorner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.minCorner, this.maxCorner);
    }
}
