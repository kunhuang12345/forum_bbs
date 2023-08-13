package com.hk.controller;

import com.hk.annotation.GlobalInterceptor;
import com.hk.annotation.VerifyParam;
import com.hk.controller.base.ABaseController;
import com.hk.entity.config.AdminConfig;
import com.hk.entity.constants.Constants;
import com.hk.entity.dto.*;
import com.hk.entity.enums.VerifyRegexEnum;
import com.hk.entity.vo.ResponseVO;
import com.hk.exception.BusinessException;
import com.hk.service.EmailCodeService;
import com.hk.service.UserInfoService;
import com.hk.utils.StringTools;
import com.hk.utils.SysCacheUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class AccountController extends ABaseController {

    @Resource
    private AdminConfig adminConfig;

    /**
     * 验证码
     *
     * @param
     * @return
     */
    @RequestMapping("/checkCode")
    public void checkCode(HttpServletResponse response, HttpSession session) throws IOException {
        CreateImageCode vCode = new CreateImageCode(130, 38, 5, 10);

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        String code = vCode.getCode();
        // 登录注册
        session.setAttribute(Constants.CHECK_CODE_KEY, code);
        vCode.write(response.getOutputStream());
    }

    @RequestMapping("/login")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO login(HttpSession session,
                            @VerifyParam(required = true, min = 5, max = 150) String account,
                            @VerifyParam(required = true, min = 8, max = 50) String password,
                            @VerifyParam(required = true) String checkCode) throws Exception {
        try {
            String checkCodeKey = (String) session.getAttribute(Constants.CHECK_CODE_KEY);
            if (!(checkCode).equalsIgnoreCase(checkCodeKey)) {
                throw new BusinessException("验证码错误");
            }

            // TODO MD5
            if (!adminConfig.getAdminAccount().equals(account) || !Objects.equals(StringTools.encodeMd5(adminConfig.getAdminPassword()), password)) {
                throw new BusinessException("账号或者密码错误");
            }
            SessionAdminUserDto sessionAdminUserDto = new SessionAdminUserDto();
            sessionAdminUserDto.setAccount(account);

            session.setAttribute(Constants.SESSION_KEY,sessionAdminUserDto);

            return getSuccessResponseVO(sessionAdminUserDto);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    @RequestMapping("/logout")
    @GlobalInterceptor()
    public ResponseVO logout(HttpSession session) {
        session.invalidate();
        return getSuccessResponseVO("已注销");
    }


}
