package com.ddylan.xlib.lunar;

import com.ddylan.xlib.Library;

public class LunarHandler {

    public LunarHandler() {
        Library.getInstance().getServer().getPluginManager().registerEvents(new LunarListener(), Library.getInstance());
    }

}
