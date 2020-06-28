package com.ddylan.xlib.command.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EasyMethod {

    private EasyClass<?> owner;
    private Method method;
    private Object[] parameters;

    public <T> EasyMethod(EasyClass tEasyClass, String name, Object[] parameters) {
        this.owner = tEasyClass;
        this.parameters = parameters;
        Class<?>[] classes = new Class[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            classes[i] = parameters[i].getClass();
            try {
                this.method = owner.getClazz().getDeclaredMethod(name, classes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public Object invoke() {
        this.method.setAccessible(true);
        if (this.method.getReturnType().equals(void.class)) {
            try {
                this.method.invoke(this.owner.get(), parameters);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }
        try {
            return this.method.getReturnType().cast(this.method.invoke(this.owner.get(), this.parameters));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

}
