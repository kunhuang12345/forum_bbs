package com.hk.entity.enums;

public enum OperateRecordOpTypeEnum {
    ARTICLE_LIKE(0,"文章点赞"),
    COMMENT_LIKE(1,"评论点赞");

    private Integer Type;
    private String desc;

    OperateRecordOpTypeEnum(Integer type, String desc) {
        Type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return Type;
    }

    public String getDesc() {
        return desc;
    }
}
