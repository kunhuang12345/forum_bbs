package com.hk.service.impl;

import java.util.List;
import java.util.Date;

import com.hk.utils.DateUtils;
import com.hk.entity.enums.DateTimePatternEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.hk.service.UserIntegralRecordService;
import com.hk.entity.po.UserIntegralRecord;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.UserIntegralRecordQuery;
import com.hk.mapper.UserIntegralRecordMapper;
import com.hk.entity.query.SimplePage;
import com.hk.entity.enums.PageSize;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description:用户积分记录表ServiceImpl
 * @author:AUTHOR
 * @date:2023/07/29
 */
@Service("userIntegralRecordService")
public class UserIntegralRecordServiceImpl implements UserIntegralRecordService {

	@Resource
	private UserIntegralRecordMapper<UserIntegralRecord, UserIntegralRecordQuery> userIntegralRecordMapper;

	/**
	 * 根据条件查询列表
	 */
	public List<UserIntegralRecord> findListByParam(UserIntegralRecordQuery query) {
		return this.userIntegralRecordMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(UserIntegralRecordQuery query) {
		return this.userIntegralRecordMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<UserIntegralRecord> findListByPage(UserIntegralRecordQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<UserIntegralRecord> list = this.findListByParam(query);
		PaginationResultVO<UserIntegralRecord> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(UserIntegralRecord bean) {
		return this.userIntegralRecordMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<UserIntegralRecord> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.userIntegralRecordMapper.insertBatch(beanList);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<UserIntegralRecord> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.userIntegralRecordMapper.insertOrUpdateBatch(beanList);
	}

	/**
	 * 根据RecordId查询
	 */
	public UserIntegralRecord getUserIntegralRecordByRecordId(Integer recordId) {
		return this.userIntegralRecordMapper.selectByRecordId(recordId);
	}

	/**
	 * 根据RecordId更新
	 */
	public Integer updateUserIntegralRecordByRecordId(UserIntegralRecord userIntegralRecord, Integer recordId) {
		return this.userIntegralRecordMapper.updateByRecordId(userIntegralRecord, recordId);
	}

	/**
	 * 根据RecordId删除
	 */
	public Integer deleteUserIntegralRecordByRecordId(Integer recordId) {
		return this.userIntegralRecordMapper.deleteByRecordId(recordId);
	}

}