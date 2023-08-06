package com.hk.service.impl;

import java.util.List;
import java.util.Date;

import com.hk.entity.constants.Constants;
import com.hk.entity.enums.*;
import com.hk.exception.BusinessException;
import com.hk.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.hk.service.ForumArticleService;
import com.hk.entity.po.ForumArticle;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.ForumArticleQuery;
import com.hk.mapper.ForumArticleMapper;
import com.hk.entity.query.SimplePage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description:文章信息ServiceImpl
 * @author:AUTHOR
 * @date:2023/07/29
 */
@Service("forumArticleService")
public class ForumArticleServiceImpl implements ForumArticleService {

	@Resource
	private ForumArticleMapper<ForumArticle, ForumArticleQuery> forumArticleMapper;

	/**
	 * 根据条件查询列表
	 */
	public List<ForumArticle> findListByParam(ForumArticleQuery query) {
		return this.forumArticleMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(ForumArticleQuery query) {
		return this.forumArticleMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<ForumArticle> findListByPage(ForumArticleQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<ForumArticle> list = this.findListByParam(query);
		PaginationResultVO<ForumArticle> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(ForumArticle bean) {
		return this.forumArticleMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<ForumArticle> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.forumArticleMapper.insertBatch(beanList);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<ForumArticle> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.forumArticleMapper.insertOrUpdateBatch(beanList);
	}

	/**
	 * 根据ArticleId查询
	 */
	public ForumArticle getForumArticleByArticleId(String articleId) {
		return this.forumArticleMapper.selectByArticleId(articleId);
	}

	/**
	 * 根据ArticleId更新
	 */
	public Integer updateForumArticleByArticleId(ForumArticle forumArticle, String articleId) {
		return this.forumArticleMapper.updateByArticleId(forumArticle, articleId);
	}

	/**
	 * 根据ArticleId删除
	 */
	public Integer deleteForumArticleByArticleId(String articleId) {
		return this.forumArticleMapper.deleteByArticleId(articleId);
	}

	public ForumArticle readArticle(String articleId) throws BusinessException {
		ForumArticle forumArticle = this.forumArticleMapper.selectByArticleId(articleId);
		if (forumArticle == null) {
			throw new BusinessException(ResponseCodeEnum.CODE_404);
		}
		if (ArticleStatusEnum.AUDIT.getStatus().equals(forumArticle.getStatus())) {
			this.forumArticleMapper.updateArticleCount(UpdateArticleCountTypeEnum.READ_COUNT.getType(), Constants.ONE,articleId);
		}
		return forumArticle;
	}

}