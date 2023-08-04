package com.hk.aspect;

import com.hk.annotation.GlobalInterceptor;
import com.hk.annotation.VerifyParam;
import com.hk.entity.enums.ResponseCodeEnum;
import com.hk.exception.BusinessException;
import com.hk.utils.StringTools;
import com.hk.utils.VerifyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
public class OperationAspect {

    private static final String[] TYPE_BASE = {"java.lang.String","java.lang.Integer","java.lang.Long"};

    private Logger logger = LoggerFactory.getLogger(OperationAspect.class);

    @Pointcut("@annotation(com.hk.annotation.GlobalInterceptor)") // 切点，拦截加上此注解内容
    private void requestInterceptor(){

    }

    @Around("requestInterceptor()")
    public Object interceptorDo(ProceedingJoinPoint point) throws BusinessException {
        try{
            Object target = point.getTarget(); // 获取到目标类对象
            Object[] arguments = point.getArgs(); // 获取到传入的参数内容
            String methodName = point.getSignature().getName();
            Class[] parameterTypes = ((MethodSignature)point.getSignature()).getMethod().getParameterTypes(); // 获取参数类型
            Method method = target.getClass().getMethod(methodName,parameterTypes);
            GlobalInterceptor interceptor = method.getAnnotation(GlobalInterceptor.class);

            if (null == interceptor) return null;

            // 校验登录
            if (interceptor.checkLogin()) {

            }
            // 校验参数
            if (interceptor.checkParams()) {
                validateParams(method,arguments);
            }
            point.proceed();
        } catch (BusinessException e){
            logger.error("业务异常",e);
            throw e;
        } catch (Exception e) {
            logger.error("全局拦截器异常",e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        } catch (Throwable throwable){
            logger.error("全局拦截器异常",throwable);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }
        return null;
    }

    public static void checkObjValue(Parameter parameter,Object value){

    }

    private void validateParams(Method method,Object[] arguments) throws BusinessException {
        Parameter[] parameters = method.getParameters();
        int i = 0;
        for (Parameter parameter : parameters) {
            VerifyParam parameterAnnotation = parameter.getAnnotation(VerifyParam.class);
            if (parameterAnnotation == null) {
                i ++;
                continue;
            }
            String typeName = parameter.getParameterizedType().getTypeName();
            if (ArrayUtils.contains(TYPE_BASE,typeName)) {
                checkValue(arguments[i],parameterAnnotation);
            }
            i ++;
        }

    }

    private void checkValue(Object value,VerifyParam verifyParam) throws BusinessException {
        Boolean isEmpty = (value == null || StringTools.isEmpty(value.toString()));
        Integer length = value == null ? 0 : value.toString().length();
        if (isEmpty && verifyParam.required()) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (isEmpty || (verifyParam.max() != -1 && verifyParam.max() < length || verifyParam.min() != -1 && verifyParam.min() > length)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (isEmpty || StringTools.isEmpty(verifyParam.regex().getRegex()) || !VerifyUtils.verify(verifyParam.regex(),String.valueOf(value))) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }


}
