package com.hk.utils;

import org.apache.commons.lang3.RandomStringUtils;

public class StringTools {
    public static Boolean isEmpty(String str){
        if (null == str || "".equals(str.trim()) || "null".equals(str)){
            return true;
        }
        return false;
    }
    public static final String getRandomString(Integer count){
        return RandomStringUtils.random(count,true,true);
    }


}
