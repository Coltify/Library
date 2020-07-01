package com.ddylan.library.uuid;

import java.util.UUID;

public interface UUIDCacheable {

    UUID uuid(String name);
    String name(UUID uuid);
    boolean ensure(UUID uuid);
    void update(UUID uuid, String name);

}
