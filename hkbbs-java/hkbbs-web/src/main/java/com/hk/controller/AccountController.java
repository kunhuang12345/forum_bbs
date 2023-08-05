package com.hk.controller;

import com.hk.annotation.GlobalInterceptor;
import com.hk.annotation.VerifyParam;
import com.hk.controller.base.ABaseController;
import com.hk.entity.constants.Constants;
import com.hk.entity.dto.CreateImageCode;
import com.hk.entity.dto.SessionWebUserDto;
import com.hk.entity.enums.ResponseCodeEnum;
import com.hk.entity.enums.VerifyRegexEnum;
import com.hk.entity.vo.ResponseVO;
import com.hk.exception.BusinessException;
import com.hk.service.EmailCodeService;
import com.hk.service.UserInfoService;
import com.hk.utils.StringTools;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
public class AccountController extends ABaseController {

    @Resource
    private EmailCodeService emailCodeService;

    @Resource
    private UserInfoService userInfoService;

    /**
     * 验证码
     * @param
     * @return
     */
    @RequestMapping("/checkCode")
    public void checkCode(HttpServletResponse response, HttpSession session,Integer type) throws IOException {
        CreateImageCode vCode = new CreateImageCode(130,38,5,10);

        response.setHeader("Pragma","no-cache");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expires",0);
        response.setContentType("image/jpeg");
        String code = vCode.getCode();
        // 登录注册
        if (type == null || type == 0){
            session.setAttribute(Constants.CHECK_CODE_KEY,code);
        } else {
            // 获取邮箱
            session.setAttribute(Constants.CHECK_CODE_KEY_EMAIL,code);
        }
        vCode.write(response.getOutputStream());
    }

    @RequestMapping("/sendEmailCode")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO sendEmailCode(HttpSession session,
                                    @VerifyParam(required = true,regex = VerifyRegexEnum.EMAIL) String email,
                                    @VerifyParam(required = true) String checkCode,
                                    @VerifyParam(required = true) Integer type) throws Exception {
        try{
            if (!checkCode.equalsIgnoreCase((String)session.getAttribute(Constants.CHECK_CODE_KEY_EMAIL))){
                throw new BusinessException("图形校验码错误");
            }
            emailCodeService.sendEmailCode(email,type);
            return getSuccessResponseVO(null);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY_EMAIL);
        }
    }


    @RequestMapping("/register")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO register(HttpSession session,
                               @VerifyParam(required = true,min = 5,max = 150,regex = VerifyRegexEnum.EMAIL) String email,
                               @VerifyParam(required = true) String emailCode,
                               @VerifyParam(required = true,min = 3,max = 30) String nickName,
                               @VerifyParam(required = true,regex = VerifyRegexEnum.PASSWORD,min = 8,max = 18) String password,
                               @VerifyParam(required = true) String checkCode) throws Exception {
        try{
            String checkCodeKey = (String)session.getAttribute(Constants.CHECK_CODE_KEY);
            if (!(checkCode).equalsIgnoreCase(checkCodeKey)) {
                throw new BusinessException("验证码错误");
            }
            userInfoService.register(email,nickName,password,emailCode);
            return getSuccessResponseVO("注册成功！");
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    @RequestMapping("/login")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO login(HttpSession session,
                            HttpServletRequest request,
                            @VerifyParam(required = true,min = 5,max = 150) String email,
                            @VerifyParam(required = true,min = 8,max = 50) String password,
                            @VerifyParam(required = true) String checkCode) throws Exception {
        try{
            String checkCodeKey = (String)session.getAttribute(Constants.CHECK_CODE_KEY);
            if (!(checkCode).equalsIgnoreCase(checkCodeKey)) {
                throw new BusinessException("验证码错误");
            }

            SessionWebUserDto sessionWebUserDto = userInfoService.login(email,password,getIpAddress(request));
            session.setAttribute(Constants.SESSION_KEY,sessionWebUserDto);

            return getSuccessResponseVO("登录成功！");
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }


}
