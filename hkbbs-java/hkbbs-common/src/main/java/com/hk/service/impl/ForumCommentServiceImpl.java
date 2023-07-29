package com.hk.service.impl;

import java.util.List;
import java.util.Date;

import com.hk.utils.DateUtils;
import com.hk.entity.enums.DateTimePatternEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.hk.service.ForumCommentService;
import com.hk.entity.po.ForumComment;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.ForumCommentQuery;
import com.hk.mapper.ForumCommentMapper;
import com.hk.entity.query.SimplePage;
import com.hk.entity.enums.PageSize;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description:评论ServiceImpl
 * @author:AUTHOR
 * @date:2023/07/29
 */
@Service("forumCommentService")
public class ForumCommentServiceImpl implements ForumCommentService {

	@Resource
	private ForumCommentMapper<ForumComment, ForumCommentQuery> forumCommentMapper;

	/**
	 * 根据条件查询列表
	 */
	public List<ForumComment> findListByParam(ForumCommentQuery query) {
		return this.forumCommentMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(ForumCommentQuery query) {
		return this.forumCommentMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<ForumComment> findListByPage(ForumCommentQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<ForumComment> list = this.findListByParam(query);
		PaginationResultVO<ForumComment> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(ForumComment bean) {
		return this.forumCommentMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<ForumComment> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.forumCommentMapper.insertBatch(beanList);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<ForumComment> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.forumCommentMapper.insertOrUpdateBatch(beanList);
	}

	/**
	 * 根据CommentId查询
	 */
	public ForumComment getForumCommentByCommentId(Integer commentId) {
		return this.forumCommentMapper.selectByCommentId(commentId);
	}

	/**
	 * 根据CommentId更新
	 */
	public Integer updateForumCommentByCommentId(ForumComment forumComment, Integer commentId) {
		return this.forumCommentMapper.updateByCommentId(forumComment, commentId);
	}

	/**
	 * 根据CommentId删除
	 */
	public Integer deleteForumCommentByCommentId(Integer commentId) {
		return this.forumCommentMapper.deleteByCommentId(commentId);
	}

}