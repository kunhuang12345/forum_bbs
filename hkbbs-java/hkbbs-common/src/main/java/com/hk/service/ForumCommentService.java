package com.hk.service;

import java.util.List;
import java.util.Date;

import com.hk.exception.BusinessException;
import com.hk.utils.DateUtils;
import com.hk.entity.enums.DateTimePatternEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.hk.entity.po.ForumComment;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.ForumCommentQuery;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description:评论Service
 * @author:AUTHOR
 * @date:2023/07/29
 */
public interface ForumCommentService {

	/**
	 * 根据条件查询列表
	 */
	List<ForumComment> findListByParam(ForumCommentQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(ForumCommentQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<ForumComment> findListByPage(ForumCommentQuery param);

	/**
	 * 新增
	 */
	Integer add(ForumComment bean);

	/**
	 * 批量新增
	 */

	Integer addBatch(List<ForumComment> beanList);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<ForumComment> beanList);

	/**
	 * 根据CommentId查询
	 */
	ForumComment getForumCommentByCommentId(Integer commentId);

	/**
	 * 根据CommentId更新
	 */
	Integer updateForumCommentByCommentId(ForumComment forumComment, Integer commentId);

	/**
	 * 根据CommentId删除
	 */
	Integer deleteForumCommentByCommentId(Integer commentId);

    void changeTopType(String userId,Integer commentId,Integer topType) throws BusinessException;

	void postComment(ForumComment comment, MultipartFile image) throws BusinessException;

	/**
	 * 删除文章
	 * */
	void delComment(String commentIds) throws BusinessException;
	void delCommentSingle(Integer commentId) throws BusinessException;

	void auditComment(String commentIds);
	void auditCommentSingle(Integer commentId);
}