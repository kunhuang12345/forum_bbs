package com.hk.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @Description:评论Mapper
 * @author:AUTHOR
 * @date:2023/07/29
 */
public interface ForumCommentMapper<T, P> extends BaseMapper<T, P> {

	/**
	 * 根据CommentId查询
	 */
	T selectByCommentId(@Param("comment_id") Integer commentId);

	/**
	 * 根据CommentId更新
	 */
	Integer updateByCommentId(@Param("bean") T t,@Param("comment_id") Integer commentId);

	/**
	 * 根据CommentId删除
	 */
	Integer deleteByCommentId(@Param("comment_id") Integer commentId);


    void updateCommentGoodCount(@Param("changeCount") Integer changeCount, @Param("commentId") Integer commentId);

    void updateTopTypeByArticleId(@Param("articleId") String articleId);

	void updateStatusBatchByUserId(@Param("status") Integer status, @Param("userId") String userId);
}