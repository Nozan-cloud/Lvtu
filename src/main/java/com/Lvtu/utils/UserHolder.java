package com.Lvtu.utils;

import com.Lvtu.dto.UserDTO;

public class UserHolder {
    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    public static void saveUser(UserDTO user){
        tl.set(user);
    }

    public static UserDTO getUser(){
        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }

    public static boolean isAdmin() {
        UserDTO user = getUser();
        return user != null && user.isAdmin();
    }
}
