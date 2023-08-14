package com.hk.service.impl;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import com.hk.entity.dto.UserMessageDto;
import com.hk.entity.enums.*;
import com.hk.exception.BusinessException;
import com.hk.service.UserInfoService;
import com.hk.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.hk.service.UserMessageService;
import com.hk.entity.po.UserMessage;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.UserMessageQuery;
import com.hk.mapper.UserMessageMapper;
import com.hk.entity.query.SimplePage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static com.hk.entity.enums.MessageTypeEnum.COMMENT;

/**
 * @Description:用户消息ServiceImpl
 * @author:AUTHOR
 * @date:2023/07/29
 */
@Service("userMessageService")
public class UserMessageServiceImpl implements UserMessageService {

    @Resource
    private UserMessageMapper<UserMessage, UserMessageQuery> userMessageMapper;

    @Resource
    private UserInfoService userInfoService;

    /**
     * 根据条件查询列表
     */
    public List<UserMessage> findListByParam(UserMessageQuery query) {
        return this.userMessageMapper.selectList(query);
    }

    /**
     * 根据条件查询数量
     */
    public Integer findCountByParam(UserMessageQuery query) {
        return this.userMessageMapper.selectCount(query);
    }

    /**
     * 分页查询
     */
    public PaginationResultVO<UserMessage> findListByPage(UserMessageQuery query) {
        Integer count = this.findCountByParam(query);
        Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
        SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
        query.setSimplePage(page);
        List<UserMessage> list = this.findListByParam(query);
        PaginationResultVO<UserMessage> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    public Integer add(UserMessage bean) {
        return this.userMessageMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    public Integer addBatch(List<UserMessage> beanList) {
        if (beanList == null || beanList.isEmpty()) {
            return 0;
        }
        return this.userMessageMapper.insertBatch(beanList);
    }

    /**
     * 批量新增或修改
     */
    public Integer addOrUpdateBatch(List<UserMessage> beanList) {
        if (beanList == null || beanList.isEmpty()) {
            return 0;
        }
        return this.userMessageMapper.insertOrUpdateBatch(beanList);
    }

    /**
     * 根据MessageId查询
     */
    public UserMessage getUserMessageByMessageId(Integer messageId) {
        return this.userMessageMapper.selectByMessageId(messageId);
    }

    /**
     * 根据MessageId更新
     */
    public Integer updateUserMessageByMessageId(UserMessage userMessage, Integer messageId) {
        return this.userMessageMapper.updateByMessageId(userMessage, messageId);
    }

    /**
     * 根据MessageId删除
     */
    public Integer deleteUserMessageByMessageId(Integer messageId) {
        return this.userMessageMapper.deleteByMessageId(messageId);
    }

    /**
     * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType查询
     */
    public UserMessage getUserMessageByArticleIdAndCommentIdAndSendUserIdAndMessageType(String articleId, Integer commentId, String sendUserId, Integer messageType) {
        return this.userMessageMapper.selectByArticleIdAndCommentIdAndSendUserIdAndMessageType(articleId, commentId, sendUserId, messageType);
    }

    /**
     * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType更新
     */
    public Integer updateUserMessageByArticleIdAndCommentIdAndSendUserIdAndMessageType(UserMessage userMessage, String articleId, Integer commentId, String sendUserId, Integer messageType) {
        return this.userMessageMapper.updateByArticleIdAndCommentIdAndSendUserIdAndMessageType(userMessage, articleId, commentId, sendUserId, messageType);
    }

    /**
     * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType删除
     */
    public Integer deleteUserMessageByArticleIdAndCommentIdAndSendUserIdAndMessageType(String articleId, Integer commentId, String sendUserId, Integer messageType) {
        return this.userMessageMapper.deleteByArticleIdAndCommentIdAndSendUserIdAndMessageType(articleId, commentId, sendUserId, messageType);
    }

    @Override
    public UserMessageDto getUserMessageCount(String userId) {
        List<Map<String, Object>> mapList = userMessageMapper.selectUserMessageCount(userId);
        UserMessageDto messageDto = new UserMessageDto();
        long totalCount = 0L;
        for (Map<String, Object> item : mapList) {
            Integer integer = (Integer) item.get("messageType");
            Long count = (Long) item.get("count");
            totalCount = totalCount + count;
            MessageTypeEnum messageTypeEnum = MessageTypeEnum.getByType(integer);
            switch (messageTypeEnum) {
                case SYS -> messageDto.setSys(count);
                case COMMENT -> messageDto.setReply(count);
                case ARTICLE_LIKE -> messageDto.setLikePost(count);
                case COMMENT_LIKE -> messageDto.setLikeComment(count);
                case DOWNLOAD_ATTACHMENT -> messageDto.setDownLoadAttachment(count);
            }
        }
        messageDto.setTotal(totalCount);
        return messageDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void readMessageByType(String receivedUserId, Integer messageType) {
        userMessageMapper.updateMessageStatusBatch(receivedUserId,messageType, MessageStatusEnum.READ.getStatus());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(String userId, String message, Integer integral) throws BusinessException {
        UserMessage userMessage = new UserMessage();
        userMessage.setReceivedUserId(userId);
        userMessage.setMessageType(MessageTypeEnum.SYS.getType());
        userMessage.setCreateTime(new Date());
        userMessage.setStatus(MessageStatusEnum.NO_READ.getStatus());
        userMessage.setMessageContent(message);
        add(userMessage);

        UserIntegralChangeTypeEnum changeTypeEnum = UserIntegralChangeTypeEnum.ADD;
        if (integral != null && integral != 0) {
            if (integral < 0) {
                integral = -1*integral;
                changeTypeEnum = UserIntegralChangeTypeEnum.REDUCE;
            }
            userInfoService.updateUserIntegral(userId,UserIntegralOperateTypeEnum.ADMIN, changeTypeEnum.getChangeType(), integral);
        }

    }

}