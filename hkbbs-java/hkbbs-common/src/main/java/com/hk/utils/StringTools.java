package com.hk.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Arrays;

public class StringTools {
    public static Boolean isEmpty(String str){
        return null == str || "".equals(str.trim()) || "null".equals(str);
    }
    public static final String getRandomString(Integer count){
        return RandomStringUtils.random(count,true,true);
    }

    public static final String getRandomNumber(Integer count){
        return RandomStringUtils.random(count,false,true);
    }

    public static final String encodeMd5(String sourceStr){
        return StringTools.isEmpty(sourceStr)? null: DigestUtils.md5Hex(sourceStr);
    }

    public static final String getFileSuffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

}
