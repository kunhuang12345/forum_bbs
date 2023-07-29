package com.hk.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @Description:文章板块信息Mapper
 * @author:AUTHOR
 * @date:2023/07/29
 */
public interface ForumBoardMapper<T, P> extends BaseMapper<T, P> {

	/**
	 * 根据BoardId查询
	 */
	T selectByBoardId(@Param("board_id") Integer boardId);

	/**
	 * 根据BoardId更新
	 */
	Integer updateByBoardId(@Param("bean") T t,@Param("board_id") Integer boardId);

	/**
	 * 根据BoardId删除
	 */
	Integer deleteByBoardId(@Param("board_id") Integer boardId);


}