package com.hk.controller;

import com.hk.annotation.GlobalInterceptor;
import com.hk.annotation.VerifyParam;
import com.hk.controller.base.ABaseController;
import com.hk.entity.config.AdminConfig;
import com.hk.entity.dto.*;
import com.hk.entity.vo.ResponseVO;
import com.hk.exception.BusinessException;
import com.hk.service.SysSettingService;
import com.hk.utils.JsonUtils;
import com.hk.utils.OKHttpUtils;
import com.hk.utils.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/setting")
public class SysSettingController extends ABaseController {

    private final static Logger logger = LoggerFactory.getLogger(SysSettingService.class);

    @Resource
    private SysSettingService sysSettingService;

    @Resource
    private AdminConfig adminConfig;

    @RequestMapping("/getSetting")
    public ResponseVO getSetting() throws BusinessException {
        SysSettingDto sysSettingDto = sysSettingService.refreshCache();
        return getSuccessResponseVO(sysSettingDto);
    }

    @RequestMapping("/saveSetting")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO saveSetting(@VerifyParam SysSetting4AuditDto auditDto,
                                  @VerifyParam SysSetting4CommentDto commentDto,
                                  @VerifyParam SysSetting4EmailDto emailDto,
                                  @VerifyParam SysSetting4PostDto postDto,
                                  @VerifyParam SysSetting4RegisterDto registerDto,
                                  @VerifyParam SysSetting4LikeDto likeDto
    ) throws BusinessException {
        SysSettingDto sysSettingDto = new SysSettingDto();
        sysSettingDto.setAuditSetting(auditDto);
        sysSettingDto.setCommentSetting(commentDto);
        sysSettingDto.setEmailSetting(emailDto);
        sysSettingDto.setPostSetting(postDto);
        sysSettingDto.setRegisterSetting(registerDto);
        sysSettingDto.setLikeSetting(likeDto);

        sysSettingService.saveSetting(sysSettingDto);
        sendWedRequest();
        return getSuccessResponseVO(null);
    }

    /**
     * 向访客端发送刷新缓存的请求
     */
    private void sendWedRequest() {
        String appKey = adminConfig.getInnerApiAppKey();
        String appSecret = adminConfig.getInnerApiAppSecret();
        Long timestamp = System.currentTimeMillis();
        String sign = StringTools.encodeMd5(appKey + timestamp +appSecret);
        String url = adminConfig.getWebApiUrl() + "?appKey=" + appKey + "&timestamp=" + timestamp + "&sign=" + sign;
        try {
            String responseJson = OKHttpUtils.getRequest(url);
            ResponseVO responseVO = JsonUtils.convertJson2Obj(responseJson, ResponseVO.class);
            if (!STATUS_SUCCESS.equals(responseVO.getStatus())) {
                throw new BusinessException("刷新访客端缓存失败");
            }
        } catch (BusinessException e) {
            logger.error("请求访客端刷新失败",e);
        }
    }

}
