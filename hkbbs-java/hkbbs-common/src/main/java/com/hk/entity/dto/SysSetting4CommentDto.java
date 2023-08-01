package com.hk.entity.dto;

/**
 * 评论设置
 */
public class SysSetting4CommentDto {
    /**
     * 评论积分
     */
    private Integer commentIntegral;

    /**
     * 评论数量阈值
     */
    private Integer commentDayCountThreshold;
    /**
     * 评论是否打开
     */
    private Boolean commentOpen;

    public Integer getCommentIntegral() {
        return commentIntegral;
    }

    public void setCommentIntegral(Integer commentIntegral) {
        this.commentIntegral = commentIntegral;
    }

    public Integer getCommentDayCountThreshold() {
        return commentDayCountThreshold;
    }

    public void setCommentDayCountThreshold(Integer commentDayCountThreshold) {
        this.commentDayCountThreshold = commentDayCountThreshold;
    }

    public Boolean getCommentOpen() {
        return commentOpen;
    }

    public void setCommentOpen(Boolean commentOpen) {
        this.commentOpen = commentOpen;
    }
}
