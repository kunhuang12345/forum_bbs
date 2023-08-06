package com.hk.service;

import java.util.List;

import com.hk.entity.dto.SessionWebUserDto;
import com.hk.entity.po.ForumArticleAttachment;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.ForumArticleAttachmentQuery;
import com.hk.exception.BusinessException;

/**
 * @Description:文件信息Service
 * @author:AUTHOR
 * @date:2023/07/29
 */
public interface ForumArticleAttachmentService {

	/**
	 * 根据条件查询列表
	 */
	List<ForumArticleAttachment> findListByParam(ForumArticleAttachmentQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(ForumArticleAttachmentQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<ForumArticleAttachment> findListByPage(ForumArticleAttachmentQuery param);

	/**
	 * 新增
	 */
	Integer add(ForumArticleAttachment bean);

	/**
	 * 批量新增
	 */

	Integer addBatch(List<ForumArticleAttachment> beanList);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<ForumArticleAttachment> beanList);

	/**
	 * 根据FileId查询
	 */
	ForumArticleAttachment getForumArticleAttachmentByFileId(String fileId);

	/**
	 * 根据FileId更新
	 */
	Integer updateForumArticleAttachmentByFileId(ForumArticleAttachment forumArticleAttachment, String fileId);

	/**
	 * 根据FileId删除
	 */
	Integer deleteForumArticleAttachmentByFileId(String fileId);

    ForumArticleAttachment downloadAttachment(String fileId, SessionWebUserDto userInfoFromSession) throws BusinessException;
}