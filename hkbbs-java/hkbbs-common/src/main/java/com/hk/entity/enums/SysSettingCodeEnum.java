package com.hk.entity.enums;

public enum SysSettingCodeEnum {

    AUDIT("audit","com.hk.entity.dto.SysSetting4AuditDto","auditSetting","审核设置"),
    COMMENT("comment","com.hk.entity.dto.SysSetting4CommentDto","commentSetting","评论设置"),
    EMAIL("email","com.hk.entity.dto.SysSetting4EmailDto","emailSetting","发送邮箱设置"),
    LIKE("like","com.hk.entity.dto.SysSetting4LikeDto","likeSetting","点赞"),
    POST("post","com.hk.entity.dto.SysSetting4PostDto","postSetting","发帖设置"),
    REGISTER("register","com.hk.entity.dto.SysSetting4RegisterDto","registerSetting","注册设置")
    ;
    private String code;
    private String clazz;
    private String propName;
    private String desc;

    public static SysSettingCodeEnum getByCode(String code){
        for (SysSettingCodeEnum item:SysSettingCodeEnum.values()){
            if (item.getCode().equals(code)){
                return item;
            }
        }
        return null;
    }

    SysSettingCodeEnum(String code, String clazz, String propName, String desc) {
        this.code = code;
        this.clazz = clazz;
        this.propName = propName;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getClazz() {
        return clazz;
    }

    public String getPropName() {
        return propName;
    }

    public String getDesc() {
        return desc;
    }
}
