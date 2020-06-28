package com.ddylan.xlib.menu;

import com.ddylan.xlib.menu.debug.DebugMenuCommand;
import com.ddylan.xlib.Library;

public class MenuHandler {

    public MenuHandler() {
        Library.getInstance().getServer().getPluginManager().registerEvents(new MenuListener(), Library.getInstance());
        Library.getInstance().getCommandHandler().registerClass(DebugMenuCommand.class);
    }

}
