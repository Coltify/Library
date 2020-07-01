package com.ddylan.library.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
public class Arguments {

    private final List<String> arguments;
    private final Set<String> flags;

    public boolean hasFlag(String flag) {
        return this.flags.contains(flag.toLowerCase());
    }

    public String join(int from, int to, char delimiter) {
        if (to > this.arguments.size() - 1 || to < 1) {
            to = this.arguments.size() - 1;
        }
        StringBuilder builder  = new StringBuilder();
        for (int i = from; i <= to; i++) {
            if (i != to) {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }

    public String join(int from, char delimiter) {
        return join(from, -1, delimiter);
    }

    public String join(int from) {
        return join(from, ' ');
    }

    public String join(char delimiter) {
        return join(0, delimiter);
    }

    public String join() {
        return join(' ');
    }

}
