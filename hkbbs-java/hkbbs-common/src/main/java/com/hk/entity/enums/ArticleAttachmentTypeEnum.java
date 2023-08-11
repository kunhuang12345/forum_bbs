package com.hk.entity.enums;

public enum ArticleAttachmentTypeEnum {
    NO_ATTACHMENT(0,"没有附件"),
    HAVE_ATTACHMENT(1,"有附件");
    private Integer type;
    private String desc;

    ArticleAttachmentTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
    public ArticleAttachmentTypeEnum getByType(Integer type) {
        for (ArticleAttachmentTypeEnum item : ArticleAttachmentTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }

    public Integer getType() {
        return type;
    }


    public String getDesc() {
        return desc;
    }
}
