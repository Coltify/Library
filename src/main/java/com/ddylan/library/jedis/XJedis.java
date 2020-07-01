package com.ddylan.library.jedis;

import com.ddylan.library.LibraryPlugin;
import com.ddylan.library.redis.RedisCommand;
import com.ddylan.library.serialization.*;
import lombok.Getter;
import net.minecraft.util.com.google.gson.Gson;
import net.minecraft.util.com.google.gson.GsonBuilder;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Getter
public final class XJedis {

    public final Gson GSON = (new GsonBuilder())
            .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
            .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public final Gson PLAIN_GSON = (new GsonBuilder())
            .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
            .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
            .serializeNulls()
            .create();

    private JedisPool localJedisPool, backboneJedisPool;
    private long localRedisLastError, backboneRedisLastError;

    public XJedis() {
        try {
            localJedisPool = new JedisPool(new JedisPoolConfig(), LibraryPlugin.getInstance().getConfig().getString("Redis.Host"), 6379, 20000, null, LibraryPlugin.getInstance().getConfig().getInt("Redis.DbId", 0));
        } catch (Exception e) {
            localJedisPool = null;
            e.printStackTrace();
            LibraryPlugin.getInstance().getLogger().warning("Couldn't connect to the Redis pool - '" + LibraryPlugin.getInstance().getConfig().getString("Redis.Host") + ":6379'");
        }

        try {
            backboneJedisPool = new JedisPool(new JedisPoolConfig(), LibraryPlugin.getInstance().getConfig().getString("BackboneRedis.Host"), 6379, 20000, null, LibraryPlugin.getInstance().getConfig().getInt("BackboneRedis.DbId", 0));
        } catch (Exception e) {
            backboneJedisPool = null;
            e.printStackTrace();
            LibraryPlugin.getInstance().getLogger().warning("Couldn't connect to the Backbone Redis pool - '" + LibraryPlugin.getInstance().getConfig().getString("BackboneRedis.Host") + ":6379'");
        }
    }

    public <T> T runRedisCommand(RedisCommand<T> redisCommand) {
        Jedis jedis = localJedisPool.getResource();
        T result = null;

        try {
            result = redisCommand.execute(jedis);
        } catch (Exception e) {
            e.printStackTrace();
            localRedisLastError = System.currentTimeMillis();

            if (jedis != null) {
                localJedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            if (jedis != null) {
                localJedisPool.returnResource(jedis);
            }
        }
        return result;
    }

    public <T> T runBackboneRedisCommand(RedisCommand<T> redisCommand) {
        Jedis jedis = backboneJedisPool.getResource();
        T result = null;

        try {
            result = redisCommand.execute(jedis);
        } catch (Exception e) {
            e.printStackTrace();
            backboneRedisLastError = System.currentTimeMillis();

            if (jedis != null) {
                backboneJedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            if (jedis != null) {
                backboneJedisPool.returnResource(jedis);
            }
        }
        return result;
    }

}