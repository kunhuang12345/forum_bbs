package com.hk.service;

import java.util.List;
import java.util.Date;

import com.hk.entity.po.ForumArticleAttachment;
import com.hk.exception.BusinessException;
import com.hk.utils.DateUtils;
import com.hk.entity.enums.DateTimePatternEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.hk.entity.po.ForumArticle;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.ForumArticleQuery;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description:文章信息Service
 * @author:AUTHOR
 * @date:2023/07/29
 */
public interface ForumArticleService {

    /**
     * 根据条件查询列表
     */
    List<ForumArticle> findListByParam(ForumArticleQuery param);

    /**
     * 根据条件查询数量
     */
    Integer findCountByParam(ForumArticleQuery param);

    /**
     * 分页查询
     */
    PaginationResultVO<ForumArticle> findListByPage(ForumArticleQuery param);

    /**
     * 新增
     */
    Integer add(ForumArticle bean);

    /**
     * 批量新增
     */

    Integer addBatch(List<ForumArticle> beanList);

    /**
     * 批量新增或修改
     */
    Integer addOrUpdateBatch(List<ForumArticle> beanList);

    /**
     * 根据ArticleId查询
     */
    ForumArticle getForumArticleByArticleId(String articleId);

    /**
     * 根据ArticleId更新
     */
    Integer updateForumArticleByArticleId(ForumArticle forumArticle, String articleId);

    /**
     * 根据ArticleId删除
     */
    Integer deleteForumArticleByArticleId(String articleId);

    ForumArticle readArticle(String articleId) throws BusinessException;

    void postArticle(Boolean isAdmin, ForumArticle article, ForumArticleAttachment articleAttachment, MultipartFile cover, MultipartFile attachment) throws BusinessException;

    void updateArticle(Boolean isAdmin, ForumArticle article, ForumArticleAttachment articleAttachment, MultipartFile cover, MultipartFile attachment) throws BusinessException;

    void delArticle(String articleIds) throws BusinessException;

    void delArticleSingle(String articleId) throws BusinessException;

    void updateBoard(String articleId, Integer pBoardId, Integer boardId) throws BusinessException;


    /**
     * 文章审核
     * */
    void auditArticle(String articleIds) throws BusinessException;
    void auditArticleSingle(String articleId) throws BusinessException;
}