package com.ddylan.xlib.serialization;

import com.ddylan.xlib.Library;
import org.bukkit.World;
import java.lang.reflect.Type;
import org.bukkit.Location;
import net.minecraft.util.com.google.gson.*;

public class LocationAdapter implements JsonDeserializer<Location>, JsonSerializer<Location> {

    public JsonElement serialize(final Location src, final Type typeOfSrc, final JsonSerializationContext context) {
        return toJson(src);
    }
    
    public Location deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        return fromJson(json);
    }
    
    public static JsonObject toJson(final Location location) {
        if (location == null) {
            return null;
        }
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("world", location.getWorld().getName());
        jsonObject.addProperty("x", location.getX());
        jsonObject.addProperty("y", location.getY());
        jsonObject.addProperty("z", location.getZ());
        jsonObject.addProperty("yaw", location.getYaw());
        jsonObject.addProperty("pitch", location.getPitch());
        return jsonObject;
    }
    
    public static Location fromJson(final JsonElement jsonElement) {
        if (jsonElement == null || !jsonElement.isJsonObject()) {
            return null;
        }
        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        final World world = Library.getInstance().getServer().getWorld(jsonObject.get("world").getAsString());
        final double x = jsonObject.get("x").getAsDouble();
        final double y = jsonObject.get("y").getAsDouble();
        final double z = jsonObject.get("z").getAsDouble();
        final float yaw = jsonObject.get("yaw").getAsFloat();
        final float pitch = jsonObject.get("pitch").getAsFloat();
        return new Location(world, x, y, z, yaw, pitch);
    }

}
