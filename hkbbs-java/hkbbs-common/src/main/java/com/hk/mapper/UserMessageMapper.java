package com.hk.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description:用户消息Mapper
 * @author:AUTHOR
 * @date:2023/07/29
 */
public interface UserMessageMapper<T, P> extends BaseMapper<T, P> {

	/**
	 * 根据MessageId查询
	 */
	T selectByMessageId(@Param("message_id") Integer messageId);

	/**
	 * 根据MessageId更新
	 */
	Integer updateByMessageId(@Param("bean") T t,@Param("message_id") Integer messageId);

	/**
	 * 根据MessageId删除
	 */
	Integer deleteByMessageId(@Param("message_id") Integer messageId);

	/**
	 * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType查询
	 */
	T selectByArticleIdAndCommentIdAndSendUserIdAndMessageType(@Param("article_id") String articleId, @Param("comment_id") Integer commentId, @Param("send_user_id") String sendUserId, @Param("message_type") Integer messageType);

	/**
	 * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType更新
	 */
	Integer updateByArticleIdAndCommentIdAndSendUserIdAndMessageType(@Param("bean") T t,@Param("article_id") String articleId, @Param("comment_id") Integer commentId, @Param("send_user_id") String sendUserId, @Param("message_type") Integer messageType);

	/**
	 * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType删除
	 */
	Integer deleteByArticleIdAndCommentIdAndSendUserIdAndMessageType(@Param("article_id") String articleId, @Param("comment_id") Integer commentId, @Param("send_user_id") String sendUserId, @Param("message_type") Integer messageType);

	List<Map<String,Object>> selectUserMessageCount(@Param("userId") String userId);

	void updateMessageStatusBatch(@Param("receivedUserId") String receivedUserId, @Param("messageType") Integer messageType, @Param("status") Integer status);

}