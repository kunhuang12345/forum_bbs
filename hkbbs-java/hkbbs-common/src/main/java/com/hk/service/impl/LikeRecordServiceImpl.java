package com.hk.service.impl;

import java.util.List;
import java.util.Date;

import com.hk.utils.DateUtils;
import com.hk.entity.enums.DateTimePatternEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.hk.service.LikeRecordService;
import com.hk.entity.po.LikeRecord;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.LikeRecordQuery;
import com.hk.mapper.LikeRecordMapper;
import com.hk.entity.query.SimplePage;
import com.hk.entity.enums.PageSize;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description:点赞记录ServiceImpl
 * @author:AUTHOR
 * @date:2023/07/29
 */
@Service("likeRecordService")
public class LikeRecordServiceImpl implements LikeRecordService {

	@Resource
	private LikeRecordMapper<LikeRecord, LikeRecordQuery> likeRecordMapper;

	/**
	 * 根据条件查询列表
	 */
	public List<LikeRecord> findListByParam(LikeRecordQuery query) {
		return this.likeRecordMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(LikeRecordQuery query) {
		return this.likeRecordMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<LikeRecord> findListByPage(LikeRecordQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<LikeRecord> list = this.findListByParam(query);
		PaginationResultVO<LikeRecord> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(LikeRecord bean) {
		return this.likeRecordMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<LikeRecord> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.likeRecordMapper.insertBatch(beanList);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<LikeRecord> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.likeRecordMapper.insertOrUpdateBatch(beanList);
	}

	/**
	 * 根据OpId查询
	 */
	public LikeRecord getLikeRecordByOpId(Integer opId) {
		return this.likeRecordMapper.selectByOpId(opId);
	}

	/**
	 * 根据OpId更新
	 */
	public Integer updateLikeRecordByOpId(LikeRecord likeRecord, Integer opId) {
		return this.likeRecordMapper.updateByOpId(likeRecord, opId);
	}

	/**
	 * 根据OpId删除
	 */
	public Integer deleteLikeRecordByOpId(Integer opId) {
		return this.likeRecordMapper.deleteByOpId(opId);
	}

	/**
	 * 根据ObjectIdAndUserIdAndOpType查询
	 */
	public LikeRecord getLikeRecordByObjectIdAndUserIdAndOpType(String objectId, String userId, Integer opType) {
		return this.likeRecordMapper.selectByObjectIdAndUserIdAndOpType(objectId, userId, opType);
	}

	/**
	 * 根据ObjectIdAndUserIdAndOpType更新
	 */
	public Integer updateLikeRecordByObjectIdAndUserIdAndOpType(LikeRecord likeRecord, String objectId, String userId, Integer opType) {
		return this.likeRecordMapper.updateByObjectIdAndUserIdAndOpType(likeRecord, objectId, userId, opType);
	}

	/**
	 * 根据ObjectIdAndUserIdAndOpType删除
	 */
	public Integer deleteLikeRecordByObjectIdAndUserIdAndOpType(String objectId, String userId, Integer opType) {
		return this.likeRecordMapper.deleteByObjectIdAndUserIdAndOpType(objectId, userId, opType);
	}

}