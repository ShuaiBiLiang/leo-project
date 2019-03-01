package com.leo.util;

import com.leo.model.domain.LeoUser;

public class UserThreadUtil {
    private static final ThreadLocal<LeoUser> contextHolder = new ThreadLocal<>();

    public static void setLeoUser(LeoUser type){
        contextHolder.set(type);
    }

    public static void clear(){
        contextHolder.remove();
    }

    public static LeoUser getLeoUser(){
        return contextHolder.get();
    }
}
