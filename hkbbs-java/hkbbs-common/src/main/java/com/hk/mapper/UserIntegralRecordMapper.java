package com.hk.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @Description:用户积分记录表Mapper
 * @author:AUTHOR
 * @date:2023/07/29
 */
public interface UserIntegralRecordMapper<T, P> extends BaseMapper<T, P> {

	/**
	 * 根据RecordId查询
	 */
	T selectByRecordId(@Param("record_id") Integer recordId);

	/**
	 * 根据RecordId更新
	 */
	Integer updateByRecordId(@Param("bean") T t,@Param("record_id") Integer recordId);

	/**
	 * 根据RecordId删除
	 */
	Integer deleteByRecordId(@Param("record_id") Integer recordId);


}