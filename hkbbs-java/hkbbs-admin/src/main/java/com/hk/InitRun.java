package com.hk;

import com.hk.service.SysSettingService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class InitRun implements ApplicationRunner {
    @Resource
    private SysSettingService sysSettingService;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        sysSettingService.refreshCache();
    }
}
