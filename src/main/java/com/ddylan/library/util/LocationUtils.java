package com.ddylan.library.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LocationUtils {

    private static final double TRUE_CIRLE = 0.5;
    private static final int FALSE_CIRCLE = 0;

    public static boolean USE_TRUE_CIRCLE = false;

//    public static Location getRandomLocationInArea(Location pointA, Location pointB) {
//        if (!pointA.getWorld().getName().equals(pointB.getWorld().getName())) {
//            return null;
//        }
//
//        double minX = Math.min(pointA.getX(), pointB.getX());
//        double minY = Math.min(pointA.getY(), pointB.getY());
//        double minZ = Math.min(pointA.getZ(), pointB.getZ());
//
//        double maxX = Math.max(pointA.getX(), pointB.getX());
//        double maxY = Math.max(pointA.getY(), pointB.getY());
//        double maxZ = Math.max(pointA.getZ(), pointB.getZ());
//
//
//        return new Location(pointA.getWorld(), NumberUtils.getRandomDouble(minX, maxX), NumberUtils.getRandomDouble(minY, maxY), NumberUtils.getRandomDouble(minZ, maxZ));
//    }

    public static boolean isInsideArea(Location loc, Location pointA, Location pointB, boolean checkY) {
        String mainLocWorldName = loc.getWorld().getName();
        //Check for same world
        if (!mainLocWorldName.equals(pointA.getWorld().getName()) || !mainLocWorldName.equals(pointB.getWorld().getName())) {
            return false;
        }

        //Check the X Coordinate.
        if ((loc.getBlockX() >= pointA.getBlockX() && loc.getBlockX() <= pointB.getBlockX()) || (loc.getBlockX() <= pointA.getBlockX() && loc.getBlockX() >= pointB.getBlockX())) {
            //Check the Z
            if ((loc.getBlockZ() >= pointA.getBlockZ() && loc.getBlockZ() <= pointB.getBlockZ()) || (loc.getBlockZ() <= pointA.getBlockZ() && loc.getBlockZ() >= pointB.getBlockZ())) {

                /* If we're not checking the y coordinate then it's inside the loc */
                if (!checkY) {
                    return true;
                }

                /* Otherwise return the results from the final check as the result. */
                return ((loc.getBlockY() >= pointA.getBlockY() && loc.getBlockY() <= pointB.getBlockY()) || (loc.getBlockY() <= pointA.getBlockY() && loc.getBlockY() >= pointB.getBlockY()));

            }
        }
        return false;
    }

    public static boolean isInRadius(Location center, Location loc, double radius) {
        if (!loc.getWorld().equals(center.getWorld())) {
            return false;
        }

        return center.distanceSquared(loc) <= (radius * radius);
    }

    public static Location getRandomLocation(Location locationCenter, double radius) {
        Random rand = new Random();
        double angle = rand.nextDouble() * 360; //Generate a random angle
        double x = locationCenter.getX() + (rand.nextDouble() * radius * Math.cos(Math.toRadians(angle)));
        double z = locationCenter.getZ() + (rand.nextDouble() * radius * Math.sin(Math.toRadians(angle)));
        double y = locationCenter.getWorld().getHighestBlockYAt((int) x, (int) z);
        return new Location(locationCenter.getWorld(), x, y, z);
    }

    /**
     * Get a full circle (not just the parameter) of !
     *
     * @param center center of the circle selection.
     * @param radius radius of the circle
     * @return a list of locations that were in the circle.
     */
    public static List<Location> getFullCircle(Location center, int radius) {
        List<Location> locs = new ArrayList<>();

        World world = center.getWorld();


        double circleSize = USE_TRUE_CIRCLE ? TRUE_CIRLE : FALSE_CIRCLE;
        final double radiusSquared = (radius + circleSize) * (radius * circleSize);

        final Vector centerPoint = center.toVector();
        final Vector currentPoint = centerPoint.clone();


        for (int x = -radius; x <= radius; x++) {
            currentPoint.setX(centerPoint.getX() + x);

            for (int z = -radius; z <= radius; z++) {
                currentPoint.setZ(centerPoint.getZ() + z);

                //If the point is within the bounds of the radius, then it's part of the circle!
                if (centerPoint.distanceSquared(currentPoint) <= radiusSquared) {
                    locs.add(currentPoint.toLocation(world));
                }
            }
        }

        return locs;
    }

    /**
     * Create a circle from a center location, and radius.
     *
     * @param centerLoc center location to build the circle from.
     * @param radius    radius of the circle (defining our circles parameter)
     * @return list of locations (only 1 line, the circles edge)
     */
    public static List<Location> getCircle(Location centerLoc, int radius) {
        List<Location> circle = new ArrayList<>();
        World world = centerLoc.getWorld();
        int x = 0;
        int z = radius;
        int error = 0;
        int d = 2 - 2 * radius;
        while (z >= 0) {
            circle.add(new Location(world, centerLoc.getBlockX() + x, centerLoc.getY(), centerLoc.getBlockZ() + z));
            circle.add(new Location(world, centerLoc.getBlockX() - x, centerLoc.getY(), centerLoc.getBlockZ() + z));
            circle.add(new Location(world, centerLoc.getBlockX() - x, centerLoc.getY(), centerLoc.getBlockZ() - z));
            circle.add(new Location(world, centerLoc.getBlockX() + x, centerLoc.getY(), centerLoc.getBlockZ() - z));
            error = 2 * (d + z) - 1;
            if ((d < 0) && (error <= 0)) {
                x++;
                d += 2 * x + 1;
            } else {
                error = 2 * (d - x) - 1;
                if ((d > 0) && (error > 0)) {
                    z--;
                    d += 1 - 2 * z;
                } else {
                    x++;
                    d += 2 * (x - z);
                    z--;
                }
            }
        }
        return circle;
    }


    /**
     * Generate a plane between the two coordinates
     *
     * @param pointA first point of the plain
     * @param pointB second point of the plain
     * @return a full (filled) plain between pointA and pointB.
     */
    public static List<Location> getPlain(Location pointA, Location pointB) {
        List<Location> plain = new ArrayList<>();
        if (pointA == null) {
            return plain;
        }
        if (pointB == null) {
            return plain;
        }
        for (int x = Math.min(pointA.getBlockX(), pointB.getBlockX()); x <= Math.max(pointA.getBlockX(), pointB.getBlockX()); x++) {
            for (int z = Math.min(pointA.getBlockZ(), pointB.getBlockZ()); z <= Math.max(pointA.getBlockZ(), pointB.getBlockZ()); z++) {
                plain.add(new Location(pointA.getWorld(), x, pointA.getBlockY(), z));
            }
        }
        return plain;
    }

    /**
     * Retrieve all locations between pointA and pointB
     *
     * @param pointA starting position of the line
     * @param pointB ending position of the line.
     * @return list of locations between pointA and pointB (forming a full line).
     */
    public static List<Location> getLine(Location pointA, Location pointB) {
        List<Location> line = new ArrayList<>();
        int dx = Math.max(pointA.getBlockX(), pointB.getBlockX()) - Math.min(pointA.getBlockX(), pointB.getBlockX());
        int dy = Math.max(pointA.getBlockY(), pointB.getBlockY()) - Math.min(pointA.getBlockY(), pointB.getBlockY());
        int dz = Math.max(pointA.getBlockZ(), pointB.getBlockZ()) - Math.min(pointA.getBlockZ(), pointB.getBlockZ());
        int x1 = pointA.getBlockX();
        int x2 = pointB.getBlockX();
        int y1 = pointA.getBlockY();
        int y2 = pointB.getBlockY();
        int z1 = pointA.getBlockZ();
        int z2 = pointB.getBlockZ();
        int x = 0;
        int y = 0;
        int z = 0;
        int i = 0;
        int d = 1;

        if ((dx >= dy) && (dx >= dz)) {
            i = 0;
            d = 1;
            if (x1 > x2) {
                d = -1;
            }
            x = pointA.getBlockX();
            do {
                i++;
                y = y1 + (x - x1) * (y2 - y1) / (x2 - x1);
                z = z1 + (x - x1) * (z2 - z1) / (x2 - x1);
                line.add(new Location(pointA.getWorld(), x, y, z));
                x += d;
            } while (i <= Math.max(x1, x2) - Math.min(x1, x2));
        } else if ((dy >= dx) && (dy >= dz)) {
            i = 0;
            d = 1;
            if (y1 > y2) {
                d = -1;
            }
            y = pointA.getBlockY();
            do {
                i++;
                x = x1 + (y - y1) * (x2 - x1) / (y2 - y1);
                z = z1 + (y - y1) * (z2 - z1) / (y2 - y1);
                line.add(new Location(pointA.getWorld(), x, y, z));
                y += d;
            } while (i <= Math.max(y1, y2) - Math.min(y1, y2));
        } else {
            i = 0;
            d = 1;
            if (z1 > z2) {
                d = -1;
            }
            z = pointA.getBlockZ();
            do {
                i++;
                y = y1 + (z - z1) * (y2 - y1) / (z2 - z1);
                x = x1 + (z - z1) * (x2 - x1) / (z2 - z1);
                line.add(new Location(pointA.getWorld(), x, y, z));
                z += d;
            } while (i <= Math.max(z1, z2) - Math.min(z1, z2));
        }

        return line;
    }
}
