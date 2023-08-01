package com.hk.entity.enums;

public enum UserStatus {
    DISABLE(0, "禁用"),
    ENABLE(1, "启用");

    private Integer status;

    private String desc;

    UserStatus(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getCode() {
        return this.status;
    }

    public String getMsg() {
        return this.desc;
    }
}
