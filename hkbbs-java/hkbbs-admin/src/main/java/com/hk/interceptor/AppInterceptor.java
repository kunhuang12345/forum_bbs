package com.hk.interceptor;

import com.hk.entity.config.AdminConfig;
import com.hk.entity.constants.Constants;
import com.hk.entity.dto.SessionAdminUserDto;
import com.hk.entity.enums.ResponseCodeEnum;
import com.hk.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 拦截器
 */
@Component("appInterceptor")
public class AppInterceptor implements HandlerInterceptor {

    @Resource
    private AdminConfig adminConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (null == handler) {
            return false;
        }
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        // 如果是登录请求，放行
        if (request.getRequestURL().indexOf("checkCode") != -1 || request.getRequestURL().indexOf("login") != -1) {
            return true;
        }
        // 如果是其它请求，检查登录状态
        checkLogin();
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    private void checkLogin() throws BusinessException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        SessionAdminUserDto sessionUser = (SessionAdminUserDto) session.getAttribute(Constants.SESSION_KEY);

        if (sessionUser == null && adminConfig.getIsDev()) {
            sessionUser = new SessionAdminUserDto();
            sessionUser.setAccount("管理员");
            session.setAttribute(Constants.SESSION_KEY,sessionUser);
        }

        if (null == sessionUser) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }

    }
}
