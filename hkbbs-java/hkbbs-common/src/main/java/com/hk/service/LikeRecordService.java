package com.hk.service;

import java.util.List;
import java.util.Date;

import com.hk.utils.DateUtils;
import com.hk.entity.enums.DateTimePatternEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.hk.entity.po.LikeRecord;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.LikeRecordQuery;

/**
 * @Description:点赞记录Service
 * @author:AUTHOR
 * @date:2023/07/29
 */
public interface LikeRecordService {

	/**
	 * 根据条件查询列表
	 */
	List<LikeRecord> findListByParam(LikeRecordQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(LikeRecordQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<LikeRecord> findListByPage(LikeRecordQuery param);

	/**
	 * 新增
	 */
	Integer add(LikeRecord bean);

	/**
	 * 批量新增
	 */

	Integer addBatch(List<LikeRecord> beanList);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<LikeRecord> beanList);

	/**
	 * 根据OpId查询
	 */
	LikeRecord getLikeRecordByOpId(Integer opId);

	/**
	 * 根据OpId更新
	 */
	Integer updateLikeRecordByOpId(LikeRecord likeRecord, Integer opId);

	/**
	 * 根据OpId删除
	 */
	Integer deleteLikeRecordByOpId(Integer opId);

	/**
	 * 根据ObjectIdAndUserIdAndOpType查询
	 */
	LikeRecord getLikeRecordByObjectIdAndUserIdAndOpType(String objectId, String userId, Integer opType);

	/**
	 * 根据ObjectIdAndUserIdAndOpType更新
	 */
	Integer updateLikeRecordByObjectIdAndUserIdAndOpType(LikeRecord likeRecord, String objectId, String userId, Integer opType);

	/**
	 * 根据ObjectIdAndUserIdAndOpType删除
	 */
	Integer deleteLikeRecordByObjectIdAndUserIdAndOpType(String objectId, String userId, Integer opType);

}