package com.hk.service;

import java.util.List;
import java.util.Date;

import com.hk.utils.DateUtils;
import com.hk.entity.enums.DateTimePatternEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.hk.entity.po.UserIntegralRecord;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.UserIntegralRecordQuery;

/**
 * @Description:用户积分记录表Service
 * @author:AUTHOR
 * @date:2023/07/29
 */
public interface UserIntegralRecordService {

	/**
	 * 根据条件查询列表
	 */
	List<UserIntegralRecord> findListByParam(UserIntegralRecordQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(UserIntegralRecordQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserIntegralRecord> findListByPage(UserIntegralRecordQuery param);

	/**
	 * 新增
	 */
	Integer add(UserIntegralRecord bean);

	/**
	 * 批量新增
	 */

	Integer addBatch(List<UserIntegralRecord> beanList);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<UserIntegralRecord> beanList);

	/**
	 * 根据RecordId查询
	 */
	UserIntegralRecord getUserIntegralRecordByRecordId(Integer recordId);

	/**
	 * 根据RecordId更新
	 */
	Integer updateUserIntegralRecordByRecordId(UserIntegralRecord userIntegralRecord, Integer recordId);

	/**
	 * 根据RecordId删除
	 */
	Integer deleteUserIntegralRecordByRecordId(Integer recordId);

}