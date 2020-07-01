package com.ddylan.library.command;

public interface Processor<T, R> {
    R process(T t);
}
