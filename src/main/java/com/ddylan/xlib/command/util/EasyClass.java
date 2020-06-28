package com.ddylan.xlib.command.util;

public class EasyClass<T> {
    Class<T> clazz;
    T object;

    public EasyClass(T object) {
        if (object != null) {
            this.clazz  = (Class)object.getClass();
        }
        this.object = object;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public T get() {
        return object;
    }

    public EasyMethod getMethod(String name, Object... parameters) {
        return new EasyMethod(this, name, parameters);
    }

    public <ST> EasyField<ST> getField(String name) {
        return new EasyField<>(this, name);
    }
}
