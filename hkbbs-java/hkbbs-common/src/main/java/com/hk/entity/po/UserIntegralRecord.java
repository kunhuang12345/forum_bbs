package com.hk.entity.po;

import java.io.Serializable;
import java.util.Date;

import com.hk.utils.DateUtils;
import com.hk.entity.enums.DateTimePatternEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;


/**
 * @Description:用户积分记录表
 * @author:AUTHOR
 * @date:2023/07/29
 */
public class UserIntegralRecord implements Serializable {

	/**
	 * 记录ID
	 */
	private Integer recordId;

	/**
	 * 用户ID
	 */
	private String userId;

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
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

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

	@Override
	public String toString() {
		return "记录ID:" + (recordId == null ? "空" : recordId) + "," + 
				"用户ID:" + (userId == null ? "空" : userId) + "," + 
				"操作类型:" + (operType == null ? "空" : operType) + "," + 
				"积分:" + (integral == null ? "空" : integral) + "," + 
				"创建时间:" + (createTime == null ? "空" : DateUtils.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_SS.getPattern()));
	}
}