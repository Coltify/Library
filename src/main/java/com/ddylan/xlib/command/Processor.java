package com.ddylan.xlib.command;

public interface Processor<T, R> {
    R process(T t);
}
