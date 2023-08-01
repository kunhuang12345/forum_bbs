package com.hk.entity.enums;

public enum UserIntegralOperateTypeEnum {
    REGISTER(1,"账号注册"),
    USER_DOWNLOAD_ATTACHMENT(2,"下载附件"),
    DOWNLOAD_ATTACHMENT(3,"附件被下载"),
    POST_COMMENT(4,"发布评论"),
    POST_ARTICLE(5,"发布文章"),
    ADMIN(6,"管理员操作"),
    DEL_ARTICLE(7,"文章被删除"),
    DEL_COMMENT(8,"评论被删除");

    private Integer operateType;
    private String desc;

    UserIntegralOperateTypeEnum(Integer operateType, String desc) {
        this.operateType = operateType;
        this.desc = desc;
    }

    public static UserIntegralOperateTypeEnum getByType(Integer type){
        for (UserIntegralOperateTypeEnum item: UserIntegralOperateTypeEnum.values()){
            if (item.getOperateType().equals(type)) {
                return item;
            }
        }
        return null;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public String getDesc() {
        return desc;
    }
}
