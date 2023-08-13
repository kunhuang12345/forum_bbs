package com.hk.aspect;

import com.hk.annotation.GlobalInterceptor;
import com.hk.annotation.VerifyParam;
import com.hk.entity.constants.Constants;
import com.hk.entity.dto.SessionWebUserDto;
import com.hk.entity.dto.SysSettingDto;
import com.hk.entity.enums.DateTimePatternEnum;
import com.hk.entity.enums.ResponseCodeEnum;
import com.hk.entity.enums.UserOperatefrequencyTypeEnum;
import com.hk.entity.query.ForumArticleQuery;
import com.hk.entity.query.ForumCommentQuery;
import com.hk.entity.query.LikeRecordQuery;
import com.hk.entity.vo.ResponseVO;
import com.hk.exception.BusinessException;
import com.hk.service.ForumArticleService;
import com.hk.service.ForumCommentService;
import com.hk.service.LikeRecordService;
import com.hk.utils.DateUtils;
import com.hk.utils.StringTools;
import com.hk.utils.SysCacheUtils;
import com.hk.utils.VerifyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Date;

@Aspect
@Component
public class OperationAspect {

    private static final String[] TYPE_BASE = {"java.lang.String", "java.lang.Integer", "java.lang.Long"};

    private Logger logger = LoggerFactory.getLogger(OperationAspect.class);

    @Resource
    private ForumArticleService forumArticleService;

    @Resource
    private ForumCommentService forumCommentService;

    @Resource
    private LikeRecordService likeRecordService;

    @Pointcut("@annotation(com.hk.annotation.GlobalInterceptor)") // 切点，拦截加上此注解内容
    private void requestInterceptor() {

    }

    @Around("requestInterceptor()")
    public void interceptorDo(ProceedingJoinPoint point) throws BusinessException {
        try {
            Object target = point.getTarget(); // 获取到目标类对象
            Object[] arguments = point.getArgs(); // 获取到传入的参数内容
            String methodName = point.getSignature().getName();
            Class[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes(); // 获取参数类型
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            GlobalInterceptor interceptor = method.getAnnotation(GlobalInterceptor.class);

            if (null == interceptor) return;

            // 校验参数
            if (interceptor.checkParams()) {
                validateParams(method, arguments);
            }

        } catch (BusinessException e) {
            logger.error("业务异常", e);
            throw e;
        } catch (Exception e) {
            logger.error("全局拦截器异常", e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        } catch (Throwable throwable) {
            logger.error("全局拦截器异常", throwable);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }
    }



    private void validateParams(Method method, Object[] arguments) throws BusinessException {
        Parameter[] parameters = method.getParameters();
        int i = 0;
        for (Parameter parameter : parameters) {
            VerifyParam parameterAnnotation = parameter.getAnnotation(VerifyParam.class);
            if (parameterAnnotation == null) {
                i++;
                continue;
            }
            String typeName = parameter.getParameterizedType().getTypeName();
            if (ArrayUtils.contains(TYPE_BASE, typeName)) {
                checkValue(arguments[i], parameterAnnotation);
            }
            i++;
        }

    }

    private void checkObjValue(Parameter parameter, Object value) throws BusinessException {
        try {
            String typeName = parameter.getParameterizedType().getTypeName();
            Class clazz = Class.forName(typeName);
            Field[] fields = clazz.getDeclaredFields();
            for (Field field:fields) {
                VerifyParam fieldVerifyParam = field.getAnnotation(VerifyParam.class);
                if (fieldVerifyParam == null) {
                    continue;
                }
                field.setAccessible(true);
                Object resultValue = field.get(value);
                checkValue(resultValue,fieldVerifyParam);
            }
        } catch (Exception e) {
            logger.error("参数校验失败",e);
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }

    private void checkValue(Object value, VerifyParam verifyParam) throws BusinessException {
        Boolean isEmpty = (value == null || StringTools.isEmpty(value.toString()));
        Integer length = value == null ? 0 : value.toString().length();
        if (isEmpty && verifyParam.required()) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if ((verifyParam.max() != -1 && verifyParam.max() < length || verifyParam.min() != -1 && verifyParam.min() > length)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 设置正则参数校验的同时参数不为空或者正则不匹配
        if (!StringTools.isEmpty(verifyParam.regex().getRegex()) && (isEmpty || !VerifyUtils.verify(verifyParam.regex(), String.valueOf(value)))) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }
}
