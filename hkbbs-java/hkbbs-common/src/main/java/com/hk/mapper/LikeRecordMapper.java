package com.hk.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @Description:点赞记录Mapper
 * @author:AUTHOR
 * @date:2023/07/29
 */
public interface LikeRecordMapper<T, P> extends BaseMapper<T, P> {

	/**
	 * 根据OpId查询
	 */
	T selectByOpId(@Param("op_id") Integer opId);

	/**
	 * 根据OpId更新
	 */
	Integer updateByOpId(@Param("bean") T t,@Param("op_id") Integer opId);

	/**
	 * 根据OpId删除
	 */
	Integer deleteByOpId(@Param("op_id") Integer opId);

	/**
	 * 根据ObjectIdAndUserIdAndOpType查询
	 */
	T selectByObjectIdAndUserIdAndOpType(@Param("object_id") String objectId, @Param("user_id") String userId, @Param("op_type") Integer opType);

	/**
	 * 根据ObjectIdAndUserIdAndOpType更新
	 */
	Integer updateByObjectIdAndUserIdAndOpType(@Param("bean") T t,@Param("object_id") String objectId, @Param("user_id") String userId, @Param("op_type") Integer opType);

	/**
	 * 根据ObjectIdAndUserIdAndOpType删除
	 */
	Integer deleteByObjectIdAndUserIdAndOpType(@Param("object_id") String objectId, @Param("user_id") String userId, @Param("op_type") Integer opType);


}