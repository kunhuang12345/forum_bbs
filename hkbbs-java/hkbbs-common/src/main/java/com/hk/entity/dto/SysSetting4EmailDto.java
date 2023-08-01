package com.hk.entity.dto;

/**
 * 发送邮箱设置
 */
public class SysSetting4EmailDto {

    // 邮件标题
    private String emailTitle;

    // 邮件内容
    public String emailContent;

    public String getEmailTitle() {
        return emailTitle;
    }

    public void setEmailTitle(String emailTitle) {
        this.emailTitle = emailTitle;
    }

    public String getEmailContent() {
        return emailContent;
    }

    public void setEmailContent(String emailContent) {
        this.emailContent = emailContent;
    }
}
