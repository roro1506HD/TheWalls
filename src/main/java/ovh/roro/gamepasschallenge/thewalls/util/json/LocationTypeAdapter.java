package ovh.roro.gamepasschallenge.thewalls.util.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class LocationTypeAdapter implements JsonDeserializer<Location> {

    @Override
    public Location deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();

        return new Location(
                object.has("world") ? Bukkit.getWorld(object.get("world").getAsString()) : null,
                object.get("x").getAsDouble(),
                object.get("y").getAsDouble(),
                object.get("z").getAsDouble(),
                object.has("yaw") ? object.get("yaw").getAsFloat() : 0.0F,
                object.has("pitch") ? object.get("pitch").getAsFloat() : 0.0F
        );
    }
}
