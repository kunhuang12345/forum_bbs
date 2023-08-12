package com.hk.entity.enums;

public enum UserOperatefrequencyTypeEnum {

    NO_CHECK(0,"不校验"),
    POST_ARTICLE(1,"发布文章"),
    POST_COMMENT(2,"评价"),
    DO_LIKE(3,"点赞2"),
    IMAGE_UPLOAD(4,"图片上传");
    private Integer operateType;
    private String desc;

    UserOperatefrequencyTypeEnum(Integer operateType, String desc) {
        this.operateType = operateType;
        this.desc = desc;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public String getDesc() {
        return desc;
    }
}
