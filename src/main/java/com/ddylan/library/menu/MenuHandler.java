package com.ddylan.library.menu;

import com.ddylan.library.menu.debug.DebugMenuCommand;
import com.ddylan.library.LibraryPlugin;

public class MenuHandler {

    public MenuHandler() {
        LibraryPlugin.getInstance().getServer().getPluginManager().registerEvents(new MenuListener(), LibraryPlugin.getInstance());
        LibraryPlugin.getInstance().getCommandHandler().registerClass(DebugMenuCommand.class);
    }

}
