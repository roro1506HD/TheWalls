package ovh.roro.gamepasschallenge.thewalls.game.team;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import ovh.roro.gamepasschallenge.thewalls.game.json.JsonDoor;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class GameDoor {

    private final JsonDoor jsonDoor;

    GameDoor(JsonDoor jsonDoor) {
        this.jsonDoor = jsonDoor;
        this.jsonDoor.fixCorners();
    }

    public void destroy() {
        Location minCorner = this.jsonDoor.getMinCorner();
        Location maxCorner = this.jsonDoor.getMaxCorner();
        World world = minCorner.getWorld();

        int minX = minCorner.getBlockX();
        int minY = minCorner.getBlockY();
        int minZ = minCorner.getBlockZ();

        int maxX = maxCorner.getBlockX();
        int maxY = maxCorner.getBlockY();
        int maxZ = maxCorner.getBlockZ();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);

                    if (this.jsonDoor.getMaterialsToDestroy().contains(block.getType())) {
                        world.playEffect(block.getLocation().clone().add(0.5, 0.5, 0.5), Effect.STEP_SOUND, block.getType());
                        block.setType(Material.AIR, false);
                    }
                }
            }
        }
    }

    public boolean contains(Location location) {
        Location minCorner = this.jsonDoor.getMinCorner();
        Location maxCorner = this.jsonDoor.getMaxCorner();

        int minX = minCorner.getBlockX();
        int minY = minCorner.getBlockY();
        int minZ = minCorner.getBlockZ();

        int maxX = maxCorner.getBlockX();
        int maxY = maxCorner.getBlockY();
        int maxZ = maxCorner.getBlockZ();

        return location.getBlockX() >= minX && location.getBlockY() >= minY && location.getBlockZ() >= minZ
                && location.getBlockX() <= maxX && location.getBlockY() <= maxY && location.getBlockZ() <= maxZ;
    }

    public JsonDoor getJsonDoor() {
        return this.jsonDoor;
    }
}
