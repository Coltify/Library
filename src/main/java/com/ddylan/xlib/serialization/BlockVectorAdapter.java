package com.ddylan.xlib.serialization;

import java.lang.reflect.Type;
import org.bukkit.util.BlockVector;
import net.minecraft.util.com.google.gson.*;

public class BlockVectorAdapter implements JsonDeserializer<BlockVector>, JsonSerializer<BlockVector>
{
    public BlockVector deserialize(final JsonElement src, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        return fromJson(src);
    }
    
    public JsonElement serialize(final BlockVector src, final Type type, final JsonSerializationContext context) {
        return toJson(src);
    }
    
    public static JsonObject toJson(final BlockVector src) {
        if (src == null) {
            return null;
        }

        final JsonObject object = new JsonObject();
        object.addProperty("x", src.getX());
        object.addProperty("y", src.getY());
        object.addProperty("z", src.getZ());
        return object;
    }
    
    public static BlockVector fromJson(final JsonElement src) {
        if (src == null || !src.isJsonObject()) {
            return null;
        }
        final JsonObject json = src.getAsJsonObject();
        final double x = json.get("x").getAsDouble();
        final double y = json.get("y").getAsDouble();
        final double z = json.get("z").getAsDouble();
        return new BlockVector(x, y, z);
    }
}
