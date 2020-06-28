package com.ddylan.xlib.serialization;

import java.lang.reflect.Type;
import net.minecraft.util.com.google.gson.*;
import org.bukkit.util.Vector;

public class VectorAdapter implements JsonDeserializer<Vector>, JsonSerializer<Vector>
{
    public Vector deserialize(final JsonElement src, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        return fromJson(src);
    }
    
    public JsonElement serialize(final Vector src, final Type type, final JsonSerializationContext context) {
        return toJson(src);
    }
    
    public static JsonObject toJson(final Vector src) {
        if (src == null) {
            return null;
        }
        final JsonObject object = new JsonObject();
        object.addProperty("x", src.getX());
        object.addProperty("y", src.getY());
        object.addProperty("z", src.getZ());
        return object;
    }
    
    public static Vector fromJson(final JsonElement src) {
        if (src == null || !src.isJsonObject()) {
            return null;
        }
        final JsonObject json = src.getAsJsonObject();
        final double x = json.get("x").getAsDouble();
        final double y = json.get("y").getAsDouble();
        final double z = json.get("z").getAsDouble();
        return new Vector(x, y, z);
    }
}
