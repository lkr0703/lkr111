package com.lkr.project2.entity;

import lombok.Data;

@Data
public class UserInfo {
    private Long id;
    private String realName;
    private String phone;
    private String address;
    private Long userId;
}