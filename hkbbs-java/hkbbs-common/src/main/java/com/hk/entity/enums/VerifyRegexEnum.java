package com.hk.entity.enums;

public enum VerifyRegexEnum {
    NO("","不校验"),
    IP("^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$","ip地址"),
    POSITIVE_INTEGER("^[1-9]\\d*$","正整数"),
    NUMBER_LETTER_LINE("^[a-zA-Z0-9_]+$","由数字，字母，下划线组成的字符串"),
    EMAIL("^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$","邮箱"),
    PHONE("^1[3-9]\\d{9}$","手机号码"),
    COMMON("^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$","数字，字母，中文，下划线"),
    PASSWORD("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\\\"\\\\|,.<>\\/?]{8,18}$","只能是数字，字母，特殊字符 8-18位"),
    ACCOUNT("^[a-zA-Z][a-zA-Z0-9_]*$","字母开头，由数字，英文字母，或者下划线组成"),
    MONEY("^\\d+(\\.\\d{1,2})?$","金额");

    private String regex;
    private String desc;

    VerifyRegexEnum(String regex,String desc){
        this.regex = regex;
        this.desc = desc;
    }

    public String getRegex() {
        return regex;
    }

    public String getDesc() {
        return desc;
    }
}
