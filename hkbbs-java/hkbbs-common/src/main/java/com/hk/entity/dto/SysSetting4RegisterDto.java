package com.hk.entity.dto;

import com.hk.annotation.VerifyParam;

public class SysSetting4RegisterDto {
    /**
     * 注册欢迎语
     */
    @VerifyParam(required = true)
    private String registerWelcomeInfo;

    public String getRegisterWelcomeInfo() {
        return registerWelcomeInfo;
    }

    public void setRegisterWelcomeInfo(String registerWelcomeInfo) {
        this.registerWelcomeInfo = registerWelcomeInfo;
    }
}
