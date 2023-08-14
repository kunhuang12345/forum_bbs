package com.hk.service;

import java.util.List;
import com.hk.entity.po.ForumBoard;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.ForumBoardQuery;
import com.hk.exception.BusinessException;

/**
 * @Description:文章板块信息Service
 * @author:AUTHOR
 * @date:2023/07/29
 */
public interface ForumBoardService {

	/**
	 * 根据条件查询列表
	 */
	List<ForumBoard> findListByParam(ForumBoardQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(ForumBoardQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<ForumBoard> findListByPage(ForumBoardQuery param);

	/**
	 * 新增
	 */
	Integer add(ForumBoard bean);

	/**
	 * 批量新增
	 */

	Integer addBatch(List<ForumBoard> beanList);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<ForumBoard> beanList);

	/**
	 * 根据BoardId查询
	 */
	ForumBoard getForumBoardByBoardId(Integer boardId);

	/**
	 * 根据BoardId更新
	 */
	Integer updateForumBoardByBoardId(ForumBoard forumBoard, Integer boardId);

	/**
	 * 根据BoardId删除
	 */
	Integer deleteForumBoardByBoardId(Integer boardId);

	/**
	 * 获取板块树
	 */
	List<ForumBoard> getBordTree(Integer postType);


    void saveForumBoard(ForumBoard forumBoard) throws BusinessException;

	void changeSort(String boardIds);
}