package com.hk.service.impl;

import java.util.List;
import java.util.Date;

import com.hk.entity.constants.Constants;
import com.hk.entity.enums.*;
import com.hk.entity.po.ForumArticle;
import com.hk.entity.po.UserMessage;
import com.hk.entity.query.ForumArticleQuery;
import com.hk.entity.query.UserMessageQuery;
import com.hk.exception.BusinessException;
import com.hk.mapper.ForumArticleMapper;
import com.hk.mapper.UserMessageMapper;
import com.hk.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.hk.service.LikeRecordService;
import com.hk.entity.po.LikeRecord;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.LikeRecordQuery;
import com.hk.mapper.LikeRecordMapper;
import com.hk.entity.query.SimplePage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @Description:点赞记录ServiceImpl
 * @author:AUTHOR
 * @date:2023/07/29
 */
@Service("likeRecordService")
public class LikeRecordServiceImpl implements LikeRecordService {

	@Resource
	private LikeRecordMapper<LikeRecord, LikeRecordQuery> likeRecordMapper;

	@Resource
	private UserMessageMapper<UserMessage, UserMessageQuery> userMessageMapper;

	@Resource
	private ForumArticleMapper<ForumArticle, ForumArticleQuery> forumArticleMapper;

	/**
	 * 根据条件查询列表
	 */
	public List<LikeRecord> findListByParam(LikeRecordQuery query) {
		return this.likeRecordMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(LikeRecordQuery query) {
		return this.likeRecordMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<LikeRecord> findListByPage(LikeRecordQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<LikeRecord> list = this.findListByParam(query);
		PaginationResultVO<LikeRecord> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(LikeRecord bean) {
		return this.likeRecordMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<LikeRecord> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.likeRecordMapper.insertBatch(beanList);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<LikeRecord> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.likeRecordMapper.insertOrUpdateBatch(beanList);
	}

	/**
	 * 根据OpId查询
	 */
	public LikeRecord getLikeRecordByOpId(Integer opId) {
		return this.likeRecordMapper.selectByOpId(opId);
	}

	/**
	 * 根据OpId更新
	 */
	public Integer updateLikeRecordByOpId(LikeRecord likeRecord, Integer opId) {
		return this.likeRecordMapper.updateByOpId(likeRecord, opId);
	}

	/**
	 * 根据OpId删除
	 */
	public Integer deleteLikeRecordByOpId(Integer opId) {
		return this.likeRecordMapper.deleteByOpId(opId);
	}

	/**
	 * 根据ObjectIdAndUserIdAndOpType查询
	 */
	public LikeRecord getLikeRecordByObjectIdAndUserIdAndOpType(String objectId, String userId, Integer opType) {
		return this.likeRecordMapper.selectByObjectIdAndUserIdAndOpType(objectId, userId, opType);
	}

	/**
	 * 根据ObjectIdAndUserIdAndOpType更新
	 */
	public Integer updateLikeRecordByObjectIdAndUserIdAndOpType(LikeRecord likeRecord, String objectId, String userId, Integer opType) {
		return this.likeRecordMapper.updateByObjectIdAndUserIdAndOpType(likeRecord, objectId, userId, opType);
	}

	/**
	 * 根据ObjectIdAndUserIdAndOpType删除
	 */
	public Integer deleteLikeRecordByObjectIdAndUserIdAndOpType(String objectId, String userId, Integer opType) {
		return this.likeRecordMapper.deleteByObjectIdAndUserIdAndOpType(objectId, userId, opType);
	}

    @Override
	@Transactional(rollbackFor = Exception.class)
    public void doLike(String objectId, String userId, String nickName, OperateRecordOpTypeEnum opTypeEnum) throws BusinessException {
		UserMessage userMessage = new UserMessage();
		userMessage.setCreateTime(new Date());
		switch (opTypeEnum) {
			case ARTICLE_LIKE:
				ForumArticle article = forumArticleMapper.selectByArticleId(objectId);
				if (article == null) {
					throw new BusinessException("文章不存在");
				}

				articleLike(article, objectId, userId, opTypeEnum);
				userMessage.setArticleId(objectId);
				userMessage.setArticleTitle(article.getTitle());
				userMessage.setMessageType(MessageTypeEnum.ARTICLE_LIKE.getType());
				userMessage.setReceivedUserId(article.getUserId());
				userMessage.setCommentId(Constants.ZERO);
				break;
			case COMMENT_LIKE:
				break;
		}
		userMessage.setSendUserId(userId);
		userMessage.setSendNickName(nickName);
		userMessage.setStatus(MessageStatusEnum.NO_READ.getStatus());
		if (!userId.equals(userMessage.getReceivedUserId())) {
			UserMessage message = userMessageMapper.selectByArticleIdAndCommentIdAndSendUserIdAndMessageType(userMessage.getArticleId(),
					userMessage.getCommentId(), userMessage.getSendUserId(), userMessage.getMessageType());
			if (message == null)
				userMessageMapper.insert(userMessage);
		}
    }

	private void articleLike(ForumArticle forumArticle, String objId, String userId, OperateRecordOpTypeEnum opTypeEnum) throws BusinessException {
		LikeRecord record = likeRecordMapper.selectByObjectIdAndUserIdAndOpType(objId, userId, opTypeEnum.getType());
		if (record != null) {
			likeRecordMapper.deleteByObjectIdAndUserIdAndOpType(objId,userId,opTypeEnum.getType());
			forumArticleMapper.updateArticleCount(UpdateArticleCountTypeEnum.GOOD_COUNT.getType(), -Constants.ONE,objId);
		} else {
			LikeRecord likeRecord = new LikeRecord();
			likeRecord.setObjectId(objId);
			likeRecord.setCreateTime(new Date());
			likeRecord.setOpType(opTypeEnum.getType());
			likeRecord.setUserId(userId);
			likeRecord.setAuthorUserId(forumArticle.getUserId());
			likeRecordMapper.insert(likeRecord);
			forumArticleMapper.updateArticleCount(UpdateArticleCountTypeEnum.GOOD_COUNT.getType(), Constants.ONE,objId);
		}
	}

}