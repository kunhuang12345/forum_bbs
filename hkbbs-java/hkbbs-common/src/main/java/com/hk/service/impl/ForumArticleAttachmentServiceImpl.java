package com.hk.service.impl;

import java.util.Date;
import java.util.List;

import com.hk.entity.dto.SessionWebUserDto;
import com.hk.entity.enums.*;
import com.hk.entity.po.*;
import com.hk.entity.query.ForumArticleAttachmentDownloadQuery;
import com.hk.entity.query.UserMessageQuery;
import com.hk.exception.BusinessException;
import com.hk.mapper.ForumArticleAttachmentDownloadMapper;
import com.hk.mapper.UserMessageMapper;
import com.hk.service.ForumArticleAttachmentService;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.ForumArticleAttachmentQuery;
import com.hk.mapper.ForumArticleAttachmentMapper;
import com.hk.entity.query.SimplePage;
import com.hk.service.ForumArticleService;
import com.hk.service.UserInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @Description:文件信息ServiceImpl
 * @author:AUTHOR
 * @date:2023/07/29
 */
@Service("forumArticleAttachmentService")
public class ForumArticleAttachmentServiceImpl implements ForumArticleAttachmentService {

	@Resource
	private ForumArticleAttachmentMapper<ForumArticleAttachment, ForumArticleAttachmentQuery> forumArticleAttachmentMapper;

	@Resource
	private ForumArticleAttachmentDownloadMapper<ForumArticleAttachmentDownload, ForumArticleAttachmentDownloadQuery> forumArticleAttachmentDownloadMapper;

	@Resource
	private UserInfoService userInfoService;

	@Resource
	private ForumArticleService forumArticleService;

	@Resource
	private UserMessageMapper<UserMessage, UserMessageQuery> userMessageMapper;

	/**
	 * 根据条件查询列表
	 */
	public List<ForumArticleAttachment> findListByParam(ForumArticleAttachmentQuery query) {
		return this.forumArticleAttachmentMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(ForumArticleAttachmentQuery query) {
		return this.forumArticleAttachmentMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<ForumArticleAttachment> findListByPage(ForumArticleAttachmentQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<ForumArticleAttachment> list = this.findListByParam(query);
		PaginationResultVO<ForumArticleAttachment> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(ForumArticleAttachment bean) {
		return this.forumArticleAttachmentMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<ForumArticleAttachment> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.forumArticleAttachmentMapper.insertBatch(beanList);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<ForumArticleAttachment> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.forumArticleAttachmentMapper.insertOrUpdateBatch(beanList);
	}

	/**
	 * 根据FileId查询
	 */
	public ForumArticleAttachment getForumArticleAttachmentByFileId(String fileId) {
		return this.forumArticleAttachmentMapper.selectByFileId(fileId);
	}

	/**
	 * 根据FileId更新
	 */
	public Integer updateForumArticleAttachmentByFileId(ForumArticleAttachment forumArticleAttachment, String fileId) {
		return this.forumArticleAttachmentMapper.updateByFileId(forumArticleAttachment, fileId);
	}

	/**
	 * 根据FileId删除
	 */
	public Integer deleteForumArticleAttachmentByFileId(String fileId) {
		return this.forumArticleAttachmentMapper.deleteByFileId(fileId);
	}

    @Override
	@Transactional(rollbackFor = Exception.class)
    public ForumArticleAttachment downloadAttachment(String fileId, SessionWebUserDto userInfoFromSession) throws BusinessException {
		ForumArticleAttachment forumArticleAttachment = forumArticleAttachmentMapper.selectByFileId(fileId);
		if (forumArticleAttachment == null) {
			throw new BusinessException("附件不存在");
		}

		ForumArticleAttachmentDownload download = null;
		if (forumArticleAttachment.getIntegral() > 0 && !userInfoFromSession.getUserId().equals(forumArticleAttachment.getUserId())) {
			download = forumArticleAttachmentDownloadMapper.selectByFileIdAndUserId(fileId,userInfoFromSession.getUserId());
			if (download == null) {
				UserInfo userInfo = userInfoService.getUserInfoByUserId(userInfoFromSession.getUserId());
				if (userInfo.getCurrentIntegral() - forumArticleAttachment.getIntegral() < 0) {
					throw new BusinessException("积分不足");
				}
				// 扣除下载人相应积分
				userInfoService.updateUserIntegral(userInfoFromSession.getUserId(), UserIntegralOperateTypeEnum.USER_DOWNLOAD_ATTACHMENT,
						UserIntegralChangeTypeEnum.REDUCE.getChangeType(), forumArticleAttachment.getIntegral());
				// 给附件提供者增加积分
				userInfoService.updateUserIntegral(forumArticleAttachment.getUserId(), UserIntegralOperateTypeEnum.DOWNLOAD_ATTACHMENT,
						UserIntegralChangeTypeEnum.ADD.getChangeType(), forumArticleAttachment.getIntegral());

				// 记录消息
				ForumArticle forumArticle = forumArticleService.getForumArticleByArticleId(forumArticleAttachment.getArticleId());
				UserMessage userMessage = new UserMessage();
				userMessage.setMessageType(MessageTypeEnum.DOWNLOAD_ATTACHMENT.getType());
				userMessage.setCreateTime(new Date());
				userMessage.setArticleId(forumArticle.getArticleId());
				userMessage.setReceivedUserId(forumArticle.getUserId());
				userMessage.setCommentId(0);
				userMessage.setSendUserId(userInfoFromSession.getUserId());
				userMessage.setSendNickName(userInfoFromSession.getNickName());
				userMessage.setStatus(MessageStatusEnum.NO_READ.getStatus());
				userMessage.setArticleTitle(forumArticle.getTitle());
				userMessageMapper.insert(userMessage);

			}
		}
		ForumArticleAttachmentDownload forumArticleAttachmentDownload = new ForumArticleAttachmentDownload();
		forumArticleAttachmentDownload.setFileId(fileId);
		forumArticleAttachmentDownload.setUserId(userInfoFromSession.getUserId());
		forumArticleAttachmentDownload.setDownloadCount(1);
		forumArticleAttachmentDownload.setArticleId(forumArticleAttachment.getArticleId());
		forumArticleAttachmentDownloadMapper.insertOrUpdate(forumArticleAttachmentDownload);
		this.forumArticleAttachmentMapper.updateDownloadCount(fileId);
        return forumArticleAttachment;
    }

}