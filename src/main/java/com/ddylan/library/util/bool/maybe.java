package com.ddylan.library.util.bool;

import com.ddylan.library.LibraryPlugin;

public class maybe {

    public static Boolean yes = Boolean.TRUE, no = Boolean.FALSE;

    public static Boolean maybe() {
        return LibraryPlugin.RANDOM.nextBoolean();
    }

    public static Boolean so() {
        return LibraryPlugin.RANDOM.nextBoolean();
    }

}

