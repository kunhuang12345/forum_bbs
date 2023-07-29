package com.hk.entity.query;

import java.util.Date;



/**
 * @Description:点赞记录查询对象
 * @author:AUTHOR
 * @date:2023/07/29
 */
public class LikeRecordQuery extends BaseQuery {

	/**
	 * 自增ID
	 */
	private Integer opId;

	/**
	 * 操作类型0:文章点赞 1:评论点赞
	 */
	private Integer opType;

	/**
	 * 主体ID
	 */
	private String objectId;

	private String objectIdFuzzy;

	/**
	 * 用户ID
	 */
	private String userId;

	private String userIdFuzzy;

	/**
	 * 发布时间
	 */
	private Date createTime;

	private String createTimeStart;

	private String createTimeEnd;

	/**
	 * 主体作者ID
	 */
	private String authorUserId;

	private String authorUserIdFuzzy;

	public void setOpId(Integer opId) {
		this.opId = opId;
	}

	public Integer getOpId() {
		return this.opId;
	}

	public void setOpType(Integer opType) {
		this.opType = opType;
	}

	public Integer getOpType() {
		return this.opType;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getObjectId() {
		return this.objectId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setAuthorUserId(String authorUserId) {
		this.authorUserId = authorUserId;
	}

	public String getAuthorUserId() {
		return this.authorUserId;
	}

	public void setObjectIdFuzzy(String objectIdFuzzy) {
		this.objectIdFuzzy = objectIdFuzzy;
	}

	public String getObjectIdFuzzy() {
		return this.objectIdFuzzy;
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

	public void setAuthorUserIdFuzzy(String authorUserIdFuzzy) {
		this.authorUserIdFuzzy = authorUserIdFuzzy;
	}

	public String getAuthorUserIdFuzzy() {
		return this.authorUserIdFuzzy;
	}

}