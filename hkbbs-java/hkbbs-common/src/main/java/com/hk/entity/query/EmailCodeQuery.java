package com.hk.entity.query;

import java.util.Date;



/**
 * @Description:邮箱验证码查询对象
 * @author:AUTHOR
 * @date:2023/07/29
 */
public class EmailCodeQuery extends BaseQuery {

	/**
	 * 邮箱
	 */
	private String email;

	private String emailFuzzy;

	/**
	 * 编号
	 */
	private String code;

	private String codeFuzzy;

	/**
	 * 创建时间
	 */
	private Date createTime;

	private String createTimeStart;

	private String createTimeEnd;

	/**
	 * 0:未使用  1:已使用
	 */
	private Integer status;

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return this.email;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setEmailFuzzy(String emailFuzzy) {
		this.emailFuzzy = emailFuzzy;
	}

	public String getEmailFuzzy() {
		return this.emailFuzzy;
	}

	public void setCodeFuzzy(String codeFuzzy) {
		this.codeFuzzy = codeFuzzy;
	}

	public String getCodeFuzzy() {
		return this.codeFuzzy;
	}

	public void setCreateTimeStart(String createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public String getCreateTimeStart() {
		return this.createTimeStart;
	}

	public void setCreateTimeEnd(String createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	public String getCreateTimeEnd() {
		return this.createTimeEnd;
	}

}