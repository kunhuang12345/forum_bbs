package com.hk.entity.enums;

public enum MessageTypeEnum {
    SYS(0,"sys","系统消息"),
    COMMENT(1,"reply","回复我的"),
    ARTICLE_LIKE(2,"likePost","赞了我的文章"),
    DOWNLOAD_ATTACHMENT(4,"downloadAttachment","下载了我的附件");


    private Integer type;
    private String code;

    MessageTypeEnum(Integer type, String code, String desc) {
        this.type = type;
        this.code = code;
        this.desc = desc;
    }

    public static MessageTypeEnum getByCode(String code){
        for (MessageTypeEnum item:MessageTypeEnum.values()){
            if (item.getCode().equals(code)){
                return item;
            }
        }
        return null;
    }

    private String desc;

    public Integer getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}