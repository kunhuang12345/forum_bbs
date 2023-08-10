package com.hk.entity.enums;

public enum AttachmentFileTypeEnum {
    ZIP(0,new String[]{".zip",".rar"}, "压缩包");
    private Integer type;
    private String[] suffix;
    private String desc;

    AttachmentFileTypeEnum(Integer type, String[] suffix, String desc) {
        this.type = type;
        this.suffix = suffix;
        this.desc = desc;
    }
    public AttachmentFileTypeEnum getByType(Integer type) {
        for (AttachmentFileTypeEnum item : AttachmentFileTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }

    public Integer getType() {
        return type;
    }

    public String[] getSuffix() {
        return suffix;
    }

    public String getDesc() {
        return desc;
    }
}
