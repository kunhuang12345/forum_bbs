package com.hk.entity.dto;

import com.hk.annotation.VerifyParam;

/**
 * 审核设置
 */
public class SysSetting4AuditDto {
    /**
     * 帖子是否需要审核
     */
    @VerifyParam(required = true)
    private Boolean postAudit;
    /**
     * 评论是需要审核
     */
    @VerifyParam(required = true)
    private Boolean commentAudit;

    public Boolean getPostAudit() {
        return postAudit;
    }

    public void setPostAudit(Boolean postAudit) {
        this.postAudit = postAudit;
    }

    public Boolean getCommentAudit() {
        return commentAudit;
    }

    public void setCommentAudit(Boolean commonAudit) {
        this.commentAudit = commonAudit;
    }
}
