package com.ddylan.xlib.command;

import com.ddylan.xlib.util.Color;

public class CommandConfiguration {

    private String noPermissionMessage;
    private String hiddenMessage;

    public String noPermissionMessage() {
        return this.noPermissionMessage;
    }

    public String hiddenMessage() {
        return this.hiddenMessage;
    }

    public CommandConfiguration setNoPermissionMessage(String noPermissionMessage) {
        this.noPermissionMessage = Color.translate(noPermissionMessage);
        return this;
    }

    public CommandConfiguration setHiddenMessage(String hiddenMessage) {
        this.hiddenMessage = hiddenMessage;
        return this;
    }

}
