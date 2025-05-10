package com.example.api_1.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBody {
    private String password;
    private String phone;
    private int id_role;
    private String nickname;
    private String email;
}

