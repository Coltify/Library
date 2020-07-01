package com.ddylan.library.lunar;

import com.ddylan.library.LibraryPlugin;

public class LunarHandler {

    public LunarHandler() {
        LibraryPlugin.getInstance().getServer().getPluginManager().registerEvents(new LunarListener(), LibraryPlugin.getInstance());
    }

}
