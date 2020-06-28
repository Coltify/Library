package com.ddylan.xlib.util.bool;

import com.ddylan.xlib.Library;

public class maybe {

    public static Boolean yes = Boolean.TRUE, no = Boolean.FALSE;

    public static Boolean maybe() {
        return Library.RANDOM.nextBoolean();
    }

    public static Boolean so() {
        return Library.RANDOM.nextBoolean();
    }

}

