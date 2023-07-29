package com.hk.entity.query;

import java.util.Date;



/**
 * @Description:用户积分记录表查询对象
 * @author:AUTHOR
 * @date:2023/07/29
 */
public class UserIntegralRecordQuery extends BaseQuery {

	/**
	 * 记录ID
	 */
	private Integer recordId;

	/**
	 * 用户ID
	 */
	private String userId;

	private String userIdFuzzy;

	/**
	 * 操作类型
	 */
	private Integer operType;

	/**
	 * 积分
	 */
	private Integer integral;

	/**
	 * 创建时间
	 */
	private Date createTime;

	private String createTimeStart;

	private String createTimeEnd;

	public void setRecordId(Integer recordId) {
		this.recordId = recordId;
	}

	public Integer getRecordId() {
		return this.recordId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setOperType(Integer operType) {
		this.operType = operType;
	}

	public Integer getOperType() {
		return this.operType;
	}

	public void setIntegral(Integer integral) {
		this.integral = integral;
	}

	public Integer getIntegral() {
		return this.integral;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setUserIdFuzzy(String userIdFuzzy) {
		this.userIdFuzzy = userIdFuzzy;
	}

	public String getUserIdFuzzy() {
		return this.userIdFuzzy;
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