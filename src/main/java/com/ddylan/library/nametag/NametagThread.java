package com.ddylan.library.nametag;

import com.ddylan.library.LibraryPlugin;
import lombok.Getter;
import lombok.Setter;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NametagThread extends Thread {

    @Getter @Setter
    private static Map<NametagUpdate, Boolean> pendingUpdates = new ConcurrentHashMap<>();

    public NametagThread() {
        super("Library - Nametag Thread");
        setDaemon(true);
    }

    public void run() {
        while(true) {
            Iterator<NametagUpdate> pendingUpdatesIterator = pendingUpdates.keySet().iterator();

            while (pendingUpdatesIterator.hasNext()) {
                NametagUpdate pendingUpdate = pendingUpdatesIterator.next();

                try {
                    LibraryPlugin.getInstance().getNametagHandler().applyUpdate(pendingUpdate);
                    pendingUpdatesIterator.remove();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(LibraryPlugin.getInstance().getNametagHandler().getUpdateInterval() * 50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
