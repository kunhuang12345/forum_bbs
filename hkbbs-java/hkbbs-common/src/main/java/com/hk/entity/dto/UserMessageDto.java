package com.hk.entity.dto;

public class UserMessageDto {
    /**
     * 总消息数
     */
    private Long total = 0L;
    /**
     * 系统消息数
     */
    private Long sys = 0L;
    /**
     * 回复消息数
     */
    public Long reply = 0L;
    /**
     * 文章点赞消息数
     */
    private Long likePost = 0L;
    /**
     * 评论点赞消息数
     */
    private Long likeComment = 0L;
    /**
     * 下载附件消息数
     */
    private Long downLoadAttachment = 0L;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getSys() {
        return sys;
    }

    public void setSys(Long sys) {
        this.sys = sys;
    }

    public Long getReply() {
        return reply;
    }

    public void setReply(Long reply) {
        this.reply = reply;
    }

    public Long getLikePost() {
        return likePost;
    }

    public void setLikePost(Long likePost) {
        this.likePost = likePost;
    }

    public Long getLikeComment() {
        return likeComment;
    }

    public void setLikeComment(Long likeComment) {
        this.likeComment = likeComment;
    }

    public Long getDownLoadAttachment() {
        return downLoadAttachment;
    }

    public void setDownLoadAttachment(Long downLoadAttachment) {
        this.downLoadAttachment = downLoadAttachment;
    }
}
