package com.hk.api;

import com.hk.annotation.GlobalInterceptor;
import com.hk.annotation.VerifyParam;
import com.hk.controller.base.ABaseController;
import com.hk.entity.config.WebConfig;
import com.hk.entity.enums.ResponseCodeEnum;
import com.hk.entity.vo.ResponseVO;
import com.hk.exception.BusinessException;
import com.hk.service.SysSettingService;
import com.hk.utils.StringTools;
import com.hk.utils.SysCacheUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/innerApi")
public class InnerApiController extends ABaseController {

    @Resource
    private WebConfig webConfig;

    @Resource
    private SysSettingService sysSettingService;

    @RequestMapping("/refresSysSetting")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO refresSysSetting (@VerifyParam(required = true) String appKey,
                                       @VerifyParam(required = true) Long timestamp,
                                       @VerifyParam(required = true) String sign) throws BusinessException {
        if (!webConfig.getInnerApiAppKey().equals(appKey)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        // 使得管理端提交数据时与进入此方法的时间不超过10s
        if (System.currentTimeMillis() - timestamp > 1000*10) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        String mySign = StringTools.encodeMd5(appKey + timestamp + webConfig.getInnerApiAppKey());

        if (!mySign.equals(sign)) {
            throw  new BusinessException(ResponseCodeEnum.CODE_600);
        }

        // 访客端刷新缓存
        sysSettingService.refreshCache();
        return getSuccessResponseVO(null);
    }


}
