package com.hk.service;

import java.util.List;
import java.util.Date;

import com.hk.entity.dto.UserMessageDto;
import com.hk.exception.BusinessException;
import com.hk.utils.DateUtils;
import com.hk.entity.enums.DateTimePatternEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.hk.entity.po.UserMessage;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.UserMessageQuery;

/**
 * @Description:用户消息Service
 * @author:AUTHOR
 * @date:2023/07/29
 */
public interface UserMessageService {

	/**
	 * 根据条件查询列表
	 */
	List<UserMessage> findListByParam(UserMessageQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(UserMessageQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserMessage> findListByPage(UserMessageQuery param);

	/**
	 * 新增
	 */
	Integer add(UserMessage bean);

	/**
	 * 批量新增
	 */

	Integer addBatch(List<UserMessage> beanList);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<UserMessage> beanList);

	/**
	 * 根据MessageId查询
	 */
	UserMessage getUserMessageByMessageId(Integer messageId);

	/**
	 * 根据MessageId更新
	 */
	Integer updateUserMessageByMessageId(UserMessage userMessage, Integer messageId);

	/**
	 * 根据MessageId删除
	 */
	Integer deleteUserMessageByMessageId(Integer messageId);

	/**
	 * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType查询
	 */
	UserMessage getUserMessageByArticleIdAndCommentIdAndSendUserIdAndMessageType(String articleId, Integer commentId, String sendUserId, Integer messageType);

	/**
	 * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType更新
	 */
	Integer updateUserMessageByArticleIdAndCommentIdAndSendUserIdAndMessageType(UserMessage userMessage, String articleId, Integer commentId, String sendUserId, Integer messageType);

	/**
	 * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType删除
	 */
	Integer deleteUserMessageByArticleIdAndCommentIdAndSendUserIdAndMessageType(String articleId, Integer commentId, String sendUserId, Integer messageType);

    UserMessageDto getUserMessageCount(String userId);

	void readMessageByType(String receivedUserId, Integer messageType);

    void sendMessage(String userId, String message, Integer integral) throws BusinessException;
}