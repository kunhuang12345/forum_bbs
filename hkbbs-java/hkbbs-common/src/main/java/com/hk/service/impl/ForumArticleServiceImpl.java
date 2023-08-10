package com.hk.service.impl;

import java.util.List;
import java.util.Date;

import com.hk.entity.constants.Constants;
import com.hk.entity.dto.FileUploadDto;
import com.hk.entity.enums.*;
import com.hk.entity.po.ForumArticleAttachment;
import com.hk.entity.po.ForumBoard;
import com.hk.entity.query.ForumArticleAttachmentQuery;
import com.hk.exception.BusinessException;
import com.hk.mapper.ForumArticleAttachmentMapper;
import com.hk.service.ForumBoardService;
import com.hk.service.UserInfoService;
import com.hk.utils.FileUtils;
import com.hk.utils.StringTools;
import com.hk.utils.SysCacheUtils;
import com.hk.service.ForumArticleService;
import com.hk.entity.po.ForumArticle;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.ForumArticleQuery;
import com.hk.mapper.ForumArticleMapper;
import com.hk.entity.query.SimplePage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    @Resource
    private ForumBoardService forumBoardService;

    @Resource
    private FileUtils fileUtils;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private ForumArticleAttachmentMapper<ForumArticleAttachment, ForumArticleAttachmentQuery> forumArticleAttachmentMapper;

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
            this.forumArticleMapper.updateArticleCount(UpdateArticleCountTypeEnum.READ_COUNT.getType(), Constants.ONE, articleId);
        }
        return forumArticle;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void postArticle(Boolean isAdmin, ForumArticle article, ForumArticleAttachment articleAttachment, MultipartFile cover, MultipartFile attachment) throws BusinessException {
        resetBoardInfo(isAdmin,article);
        Date curDate = new Date();
        String articleId = StringTools.getRandomString(Constants.LENGTH_15);
        article.setArticleId(articleId);
        article.setPostTime(curDate);
        article.setLastUpdateTime(curDate);

        // 上传封面
        if (cover != null) {
            FileUploadDto fileUploadDto = fileUtils.uploadFile2Local(cover,Constants.FILE_FOLDER_IMAGE,FileUploadTypeEnum.ARTICLE_COVER);
            article.setCover(fileUploadDto.getLocalPath());
        }

        // 上传附件
        if (attachment != null) {
            uploadAttachment(article,articleAttachment,attachment,false);
            article.setAttachmentType(ArticleAttachmentTypeEnum.HAVE_ATTACHMENT.getType());
        } else {
            article.setAttachmentType(ArticleAttachmentTypeEnum.NO_ATTACHMENT.getType());
        }

        // 文章审核信息
        if (isAdmin) {
            article.setStatus(ArticleStatusEnum.AUDIT.getStatus());
        } else {
            Boolean postAudit = SysCacheUtils.getSysSetting().getAuditSetting().getPostAudit();
            article.setStatus(postAudit? ArticleStatusEnum.NO_AUDIT.getStatus() : ArticleStatusEnum.AUDIT.getStatus());
        }

        // 替换图片
        String content = article.getContent();
        if (!StringTools.isEmpty(content)) {
//            String month =
        }

        forumArticleMapper.insert(article);

        // 增加积分
        Integer postIntegral = SysCacheUtils.getSysSetting().getPostSetting().getPostIntegral();
        if (postIntegral > 0 && ArticleStatusEnum.AUDIT.getStatus().equals(article.getStatus())) {
            userInfoService.updateUserIntegral(article.getUserId(), UserIntegralOperateTypeEnum.POST_ARTICLE, UserIntegralChangeTypeEnum.ADD.getChangeType(), postIntegral);
        }
    }

    /**
     * 设置板块信息
     */
    private void resetBoardInfo(Boolean isAdmin, ForumArticle forumArticle) throws BusinessException {
        ForumBoard pBoard = forumBoardService.getForumBoardByBoardId(forumArticle.getPBoardId());
        if (pBoard == null || (Constants.ZERO.equals(pBoard.getPostType()) && !isAdmin)) {
            throw new BusinessException("一级板块不存在");
        }
        forumArticle.setPBoardName(pBoard.getBoardName());


        if (forumArticle.getBoardId() != null || forumArticle.getBoardId() != 0) {
            ForumBoard board = forumBoardService.getForumBoardByBoardId(forumArticle.getBoardId());
            if (board == null || board.getPostType().equals(Constants.ONE) && !isAdmin) {
                throw new BusinessException("二级板块不存在");
            }
        }
        forumArticle.setBoardName(forumArticle.getBoardName());
    }

    /**
     * 上传附件
     */
    private void uploadAttachment(ForumArticle article, ForumArticleAttachment articleAttachment, MultipartFile file, Boolean isUpdate) throws BusinessException {
        // 获取系统最大附件限制
        Integer allowSizeMb = SysCacheUtils.getSysSetting().getPostSetting().getAttachmentSize();
        long allowSize = (long)allowSizeMb*Constants.FILE_SIZE_1M;

        if (file.getSize() > allowSize) {
            throw new BusinessException("附件最大上传大小:" + allowSizeMb + "MB");
        }

        // 如果是用户对文件进行修改
        if (isUpdate) {

        }
        FileUploadDto fileUploadDto = fileUtils.uploadFile2Local(file, Constants.FILE_FOLDER_ATTACHMENT,FileUploadTypeEnum.ARTICLE_ATTACHMENT);

        articleAttachment.setFileId(StringTools.getRandomNumber(Constants.LENGTH_15));
        articleAttachment.setArticleId(article.getArticleId());
        articleAttachment.setFileName(fileUploadDto.getOriginalFileName());
        articleAttachment.setFilePath(fileUploadDto.getLocalPath());
        articleAttachment.setFileSize(file.getSize());
        articleAttachment.setDownloadCount(Constants.ZERO);
        articleAttachment.setFileType(AttachmentFileTypeEnum.ZIP.getType());
        articleAttachment.setUserId(article.getUserId());
        forumArticleAttachmentMapper.insert(articleAttachment);
    }

}