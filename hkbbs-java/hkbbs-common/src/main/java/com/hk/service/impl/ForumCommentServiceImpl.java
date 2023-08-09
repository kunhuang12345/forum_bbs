package com.hk.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import com.hk.entity.enums.CommentTopTypeEnum;
import com.hk.entity.enums.ResponseCodeEnum;
import com.hk.entity.po.ForumArticle;
import com.hk.entity.query.ForumArticleQuery;
import com.hk.exception.BusinessException;
import com.hk.mapper.ForumArticleMapper;
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
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @Description:评论ServiceImpl
 * @author:AUTHOR
 * @date:2023/07/29
 */
@Service("forumCommentService")
public class ForumCommentServiceImpl implements ForumCommentService {

	@Resource
	private ForumArticleMapper<ForumArticle, ForumArticleQuery> forumArticleMapper;

	@Resource
	private ForumCommentMapper<ForumComment, ForumCommentQuery> forumCommentMapper;

	/**
	 * 根据条件查询列表
	 */
	public List<ForumComment> findListByParam(ForumCommentQuery query) {

		List<ForumComment> list = forumCommentMapper.selectList(query);

		// 获取二级评论
		if (query.getLoadChildren() != null && query.getLoadChildren()) {
			ForumCommentQuery subQuery = new ForumCommentQuery();
			subQuery.setQueryLikeType(query.getQueryLikeType());
			subQuery.setCurrentUserId(query.getCurrentUserId());
			subQuery.setArticleId(query.getArticleId());
			subQuery.setStatus(query.getStatus());
			List<Integer> pcommentIdList = list.stream().map(ForumComment::getCommentId).distinct().collect(Collectors.toList());
			subQuery.setPcommentIdList(pcommentIdList);
			List<ForumComment> forumCommentList = forumCommentMapper.selectList(subQuery);

			// 将p_comment_id存入integer，子评论存入List集合中
			Map<Integer,List<ForumComment>> tempMap = forumCommentList.stream().collect(Collectors.groupingBy(ForumComment::getPCommentId));
			list.forEach(item -> {
				item.setChildren(tempMap.get(item.getCommentId()));
			});
		}

		return list;
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

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void changeTopType(String userId, Integer commentId, Integer topType) throws BusinessException {
		CommentTopTypeEnum topTypeEnum = CommentTopTypeEnum.getByType(topType);
		// 错误的置顶类型
		if (topTypeEnum == null) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		ForumComment forumComment = forumCommentMapper.selectByCommentId(commentId);
		// 错误的commentId
		if (forumComment == null) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}

		ForumArticle forumArticle = forumArticleMapper.selectByArticleId(forumComment.getArticleId());
		// 文章不存在
		if (forumArticle == null) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}

		// 不是本人操作
		if (!forumArticle.getUserId().equals(userId) || forumComment.getPCommentId() != 0) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}

		// 已实现直接返回
		if (forumComment.getTopType().equals(topType)) {
			return;
		}

		if (CommentTopTypeEnum.TOP.getType().equals(topType)) {
			forumCommentMapper.updateTopTypeByArticleId(forumArticle.getArticleId());
		}
		ForumComment updateInfo = new ForumComment();
		updateInfo.setTopType(topType);
		forumCommentMapper.updateByCommentId(updateInfo,commentId);
	}

}