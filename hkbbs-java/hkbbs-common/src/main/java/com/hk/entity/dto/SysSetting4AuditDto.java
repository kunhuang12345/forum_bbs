package com.hk.entity.dto;

/**
 * 审核设置
 */
public class SysSetting4AuditDto {
    /**
     * 帖子是否需要审核
     */
    private Boolean postAudit;
    /**
     * 评论是需要审核
     */
    private Boolean commonAudit;

    public Boolean getPostAudit() {
        return postAudit;
    }

    public void setPostAudit(Boolean postAudit) {
        this.postAudit = postAudit;
    }

    public Boolean getCommonAudit() {
        return commonAudit;
    }

    public void setCommonAudit(Boolean commonAudit) {
        this.commonAudit = commonAudit;
    }
}
