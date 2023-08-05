package com.hk.entity.enums;

public enum UserStatusEnum {
    DISABLE(0, "禁用"),
    ENABLE(1, "启用");

    private Integer status;

    private String desc;

    UserStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getDesc() {
        return this.desc;
    }
}
