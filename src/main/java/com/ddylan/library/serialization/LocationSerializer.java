package com.ddylan.library.serialization;

import com.ddylan.library.LibraryPlugin;
import com.mongodb.BasicDBObject;
import org.bukkit.Location;
import org.bukkit.World;

public final class LocationSerializer
{
    private LocationSerializer() {
    }
    
    public static BasicDBObject serialize(final Location location) {
        if (location == null) {
            return new BasicDBObject();
        }
        final BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("world", location.getWorld().getName());
        dbObject.put("x", location.getX());
        dbObject.put("y", location.getY());
        dbObject.put("z", location.getZ());
        dbObject.append("yaw", location.getYaw());
        dbObject.append("pitch", location.getPitch());
        return dbObject;
    }
    
    public static Location deserialize(final BasicDBObject dbObject) {
        if (dbObject == null || dbObject.isEmpty()) {
            return null;
        }
        final World world = LibraryPlugin.getInstance().getServer().getWorld(dbObject.getString("world"));
        final double x = dbObject.getDouble("x");
        final double y = dbObject.getDouble("y");
        final double z = dbObject.getDouble("z");
        final int yaw = dbObject.getInt("yaw");
        final int pitch = dbObject.getInt("pitch");
        return new Location(world, x, y, z, (float)yaw, (float)pitch);
    }
}
