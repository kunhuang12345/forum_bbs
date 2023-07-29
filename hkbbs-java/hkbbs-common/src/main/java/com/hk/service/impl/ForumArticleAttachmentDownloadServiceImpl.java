package com.hk.service.impl;

import java.util.List;
import com.hk.service.ForumArticleAttachmentDownloadService;
import com.hk.entity.po.ForumArticleAttachmentDownload;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.ForumArticleAttachmentDownloadQuery;
import com.hk.mapper.ForumArticleAttachmentDownloadMapper;
import com.hk.entity.query.SimplePage;
import com.hk.entity.enums.PageSize;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description:用户附件下载ServiceImpl
 * @author:AUTHOR
 * @date:2023/07/29
 */
@Service("forumArticleAttachmentDownloadService")
public class ForumArticleAttachmentDownloadServiceImpl implements ForumArticleAttachmentDownloadService {

	@Resource
	private ForumArticleAttachmentDownloadMapper<ForumArticleAttachmentDownload, ForumArticleAttachmentDownloadQuery> forumArticleAttachmentDownloadMapper;

	/**
	 * 根据条件查询列表
	 */
	public List<ForumArticleAttachmentDownload> findListByParam(ForumArticleAttachmentDownloadQuery query) {
		return this.forumArticleAttachmentDownloadMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(ForumArticleAttachmentDownloadQuery query) {
		return this.forumArticleAttachmentDownloadMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<ForumArticleAttachmentDownload> findListByPage(ForumArticleAttachmentDownloadQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<ForumArticleAttachmentDownload> list = this.findListByParam(query);
		PaginationResultVO<ForumArticleAttachmentDownload> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(ForumArticleAttachmentDownload bean) {
		return this.forumArticleAttachmentDownloadMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<ForumArticleAttachmentDownload> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.forumArticleAttachmentDownloadMapper.insertBatch(beanList);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<ForumArticleAttachmentDownload> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.forumArticleAttachmentDownloadMapper.insertOrUpdateBatch(beanList);
	}

	/**
	 * 根据FileIdAndUserId查询
	 */
	public ForumArticleAttachmentDownload getForumArticleAttachmentDownloadByFileIdAndUserId(String fileId, String userId) {
		return this.forumArticleAttachmentDownloadMapper.selectByFileIdAndUserId(fileId, userId);
	}

	/**
	 * 根据FileIdAndUserId更新
	 */
	public Integer updateForumArticleAttachmentDownloadByFileIdAndUserId(ForumArticleAttachmentDownload forumArticleAttachmentDownload, String fileId, String userId) {
		return this.forumArticleAttachmentDownloadMapper.updateByFileIdAndUserId(forumArticleAttachmentDownload, fileId, userId);
	}

	/**
	 * 根据FileIdAndUserId删除
	 */
	public Integer deleteForumArticleAttachmentDownloadByFileIdAndUserId(String fileId, String userId) {
		return this.forumArticleAttachmentDownloadMapper.deleteByFileIdAndUserId(fileId, userId);
	}

}