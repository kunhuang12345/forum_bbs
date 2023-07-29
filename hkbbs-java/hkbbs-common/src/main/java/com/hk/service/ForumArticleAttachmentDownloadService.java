package com.hk.service;

import java.util.List;
import com.hk.entity.po.ForumArticleAttachmentDownload;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.ForumArticleAttachmentDownloadQuery;

/**
 * @Description:用户附件下载Service
 * @author:AUTHOR
 * @date:2023/07/29
 */
public interface ForumArticleAttachmentDownloadService {

	/**
	 * 根据条件查询列表
	 */
	List<ForumArticleAttachmentDownload> findListByParam(ForumArticleAttachmentDownloadQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(ForumArticleAttachmentDownloadQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<ForumArticleAttachmentDownload> findListByPage(ForumArticleAttachmentDownloadQuery param);

	/**
	 * 新增
	 */
	Integer add(ForumArticleAttachmentDownload bean);

	/**
	 * 批量新增
	 */

	Integer addBatch(List<ForumArticleAttachmentDownload> beanList);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<ForumArticleAttachmentDownload> beanList);

	/**
	 * 根据FileIdAndUserId查询
	 */
	ForumArticleAttachmentDownload getForumArticleAttachmentDownloadByFileIdAndUserId(String fileId, String userId);

	/**
	 * 根据FileIdAndUserId更新
	 */
	Integer updateForumArticleAttachmentDownloadByFileIdAndUserId(ForumArticleAttachmentDownload forumArticleAttachmentDownload, String fileId, String userId);

	/**
	 * 根据FileIdAndUserId删除
	 */
	Integer deleteForumArticleAttachmentDownloadByFileIdAndUserId(String fileId, String userId);

}