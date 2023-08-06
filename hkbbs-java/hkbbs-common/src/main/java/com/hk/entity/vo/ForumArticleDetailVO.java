package com.hk.entity.vo;


public class ForumArticleDetailVO {
    private ForumArticleVO ForumArticle;
    private ForumArticleAttachmentVO attachment;
    private Boolean haveLike;

    public ForumArticleVO getForumArticle() {
        return ForumArticle;
    }

    public void setForumArticle(ForumArticleVO forumArticle) {
        ForumArticle = forumArticle;
    }

    public ForumArticleAttachmentVO getAttachment() {
        return attachment;
    }

    public void setAttachment(ForumArticleAttachmentVO attachment) {
        this.attachment = attachment;
    }

    public Boolean getHaveLike() {
        return haveLike;
    }

    public void setHaveLike(Boolean haveLike) {
        this.haveLike = haveLike;
    }
}
