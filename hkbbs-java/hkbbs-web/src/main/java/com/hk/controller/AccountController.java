package com.hk.controller;

import com.hk.entity.constants.Constants;
import com.hk.entity.dto.CreateImageCode;
import com.hk.entity.enums.ResponseCodeEnum;
import com.hk.entity.po.UserIntegralRecord;
import com.hk.entity.vo.ResponseVO;
import com.hk.exception.BusinessException;
import com.hk.mapper.EmailCodeMapper;
import com.hk.service.EmailCodeService;
import com.hk.service.UserInfoService;
import com.hk.service.UserIntegralRecordService;
import com.hk.service.impl.UserIntegralRecordServiceImpl;
import com.hk.utils.StringTools;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

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
    public ResponseVO sendEmailCode(HttpSession session,String email,String checkCode,Integer type) throws Exception {

        try{
            if (StringTools.isEmpty(email) || StringTools.isEmpty(checkCode) || type == null) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
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
    public ResponseVO register(HttpSession session,String email,String emailCode,String nickName,String password,String checkCode) throws Exception {
        try{
            if (StringTools.isEmpty(email) || StringTools.isEmpty(emailCode) || StringTools.isEmpty(nickName) || StringTools.isEmpty(password) || StringTools.isEmpty(checkCode)){
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            String checkCodeKey = (String)session.getAttribute(Constants.CHECK_CODE_KEY);
            if (!(checkCodeKey).equalsIgnoreCase(checkCode)) {
                throw new BusinessException("验证码错误");
            }
            userInfoService.register(email,nickName,password,emailCode);
            return getSuccessResponseVO(null);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }


}
