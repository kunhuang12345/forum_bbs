package com.hk.service.impl;

import java.util.List;
import com.hk.service.ForumArticleAttachmentService;
import com.hk.entity.po.ForumArticleAttachment;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.ForumArticleAttachmentQuery;
import com.hk.mapper.ForumArticleAttachmentMapper;
import com.hk.entity.query.SimplePage;
import com.hk.entity.enums.PageSize;
import org.springframework.stereotype.Service;

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

}