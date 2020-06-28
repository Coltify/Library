package com.ddylan.xlib.command.parameter.filter;

import java.util.regex.Pattern;

public class NormalFilter extends BaseFilter {

    public NormalFilter() {
        this.bannedPatterns.add(Pattern.compile("n+[i1l|]+gg+[e3]+r+"));
        this.bannedPatterns.add(Pattern.compile("k+i+l+l+ *y*o*u+r+ *s+e+l+f+"));
        this.bannedPatterns.add(Pattern.compile("f+a+g+[o0]+t+"));
        this.bannedPatterns.add(Pattern.compile("\\bk+y+s+\\b"));
        this.bannedPatterns.add(Pattern.compile("b+e+a+n+e+r+"));
        this.bannedPatterns.add(Pattern.compile("\\d{1,3}[,.]\\d{1,3}[,.]\\d{1,3}[,.]\\d{1,3}"));
        this.bannedPatterns.add(Pattern.compile("optifine\\.(?=\\w+)(?!net)"));
        this.bannedPatterns.add(Pattern.compile("gyazo\\.(?=\\w+)(?!com)"));
        this.bannedPatterns.add(Pattern.compile("prntscr\\.(?=\\w+)(?!com)"));
    }

}
