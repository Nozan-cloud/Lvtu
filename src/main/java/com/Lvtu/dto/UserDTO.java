package com.Lvtu.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String nickName;
    private String icon;
    private int role;
    public boolean isAdmin() {
        return role == 1;
    }
}
