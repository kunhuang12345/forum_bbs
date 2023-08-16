package com.hk.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.hk.entity.constants.Constants;
import com.hk.entity.dto.FileUploadDto;
import com.hk.entity.enums.*;
import com.hk.entity.po.ForumArticle;
import com.hk.entity.po.UserInfo;
import com.hk.entity.po.UserMessage;
import com.hk.entity.query.ForumArticleQuery;
import com.hk.exception.BusinessException;
import com.hk.mapper.ForumArticleMapper;
import com.hk.service.UserInfoService;
import com.hk.service.UserMessageService;
import com.hk.utils.FileUtils;
import com.hk.utils.StringTools;
import com.hk.service.ForumCommentService;
import com.hk.entity.po.ForumComment;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.ForumCommentQuery;
import com.hk.mapper.ForumCommentMapper;
import com.hk.entity.query.SimplePage;
import com.hk.utils.SysCacheUtils;
import org.apache.tomcat.util.bcel.classfile.Constant;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Description:评论ServiceImpl
 * @author:AUTHOR
 * @date:2023/07/29
 */
@Service("forumCommentService")
public class ForumCommentServiceImpl implements ForumCommentService {

    @Resource
    private ForumArticleMapper<ForumArticle, ForumArticleQuery> forumArticleMapper;

    @Resource
    private ForumCommentMapper<ForumComment, ForumCommentQuery> forumCommentMapper;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserMessageService userMessageService;

    @Resource
    private FileUtils fileUtils;

    @Lazy
    @Resource
    private ForumCommentService forumCommentService;

    /**
     * 根据条件查询列表
     */
    public List<ForumComment> findListByParam(ForumCommentQuery query) {

        List<ForumComment> list = forumCommentMapper.selectList(query);

        // 获取二级评论
        if (query.getLoadChildren() != null && query.getLoadChildren()) {
            ForumCommentQuery subQuery = new ForumCommentQuery();
            subQuery.setQueryLikeType(query.getQueryLikeType());
            subQuery.setCurrentUserId(query.getCurrentUserId());
            subQuery.setArticleId(query.getArticleId());
            subQuery.setStatus(query.getStatus());
            List<Integer> pcommentIdList = list.stream().map(ForumComment::getCommentId).distinct().collect(Collectors.toList());
            subQuery.setPcommentIdList(pcommentIdList);
            List<ForumComment> forumCommentList = forumCommentMapper.selectList(subQuery);

            // 将p_comment_id存入integer，子评论存入List集合中
            Map<Integer, List<ForumComment>> tempMap = forumCommentList.stream().collect(Collectors.groupingBy(ForumComment::getPCommentId));
            list.forEach(item -> {
                item.setChildren(tempMap.get(item.getCommentId()));
            });
        }

        return list;
    }

    /**
     * 根据条件查询数量
     */
    public Integer findCountByParam(ForumCommentQuery query) {
        return this.forumCommentMapper.selectCount(query);
    }

    /**
     * 分页查询
     */
    public PaginationResultVO<ForumComment> findListByPage(ForumCommentQuery query) {
        Integer count = this.findCountByParam(query);
        Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
        SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
        query.setSimplePage(page);
        List<ForumComment> list = this.findListByParam(query);
        PaginationResultVO<ForumComment> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    public Integer add(ForumComment bean) {
        return this.forumCommentMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    public Integer addBatch(List<ForumComment> beanList) {
        if (beanList == null || beanList.isEmpty()) {
            return 0;
        }
        return this.forumCommentMapper.insertBatch(beanList);
    }

    /**
     * 批量新增或修改
     */
    public Integer addOrUpdateBatch(List<ForumComment> beanList) {
        if (beanList == null || beanList.isEmpty()) {
            return 0;
        }
        return this.forumCommentMapper.insertOrUpdateBatch(beanList);
    }

    /**
     * 根据CommentId查询
     */
    public ForumComment getForumCommentByCommentId(Integer commentId) {
        return this.forumCommentMapper.selectByCommentId(commentId);
    }

    /**
     * 根据CommentId更新
     */
    public Integer updateForumCommentByCommentId(ForumComment forumComment, Integer commentId) {
        return this.forumCommentMapper.updateByCommentId(forumComment, commentId);
    }

    /**
     * 根据CommentId删除
     */
    public Integer deleteForumCommentByCommentId(Integer commentId) {
        return this.forumCommentMapper.deleteByCommentId(commentId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void changeTopType(String userId, Integer commentId, Integer topType) throws BusinessException {
        CommentTopTypeEnum topTypeEnum = CommentTopTypeEnum.getByType(topType);
        // 错误的置顶类型
        if (topTypeEnum == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        ForumComment forumComment = forumCommentMapper.selectByCommentId(commentId);
        // 错误的commentId
        if (forumComment == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        ForumArticle forumArticle = forumArticleMapper.selectByArticleId(forumComment.getArticleId());
        // 文章不存在
        if (forumArticle == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        // 不是本人操作
        if (!forumArticle.getUserId().equals(userId) || forumComment.getPCommentId() != 0) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        // 已实现直接返回
        if (forumComment.getTopType().equals(topType)) {
            return;
        }

        if (CommentTopTypeEnum.TOP.getType().equals(topType)) {
            forumCommentMapper.updateTopTypeByArticleId(forumArticle.getArticleId());
        }
        ForumComment updateInfo = new ForumComment();
        updateInfo.setTopType(topType);
        forumCommentMapper.updateByCommentId(updateInfo, commentId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void postComment(ForumComment comment, MultipartFile image) throws BusinessException {
        ForumArticle forumArticle = forumArticleMapper.selectByArticleId(comment.getArticleId());
        // 判断回复的文章状态
        if (forumArticle == null || !ArticleStatusEnum.AUDIT.getStatus().equals(forumArticle.getStatus())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        ForumComment pComment = null;
        if (comment.getPCommentId() != 0) {
            pComment = forumCommentMapper.selectByCommentId(comment.getPCommentId());
            // 判断回复的评论状态
            if (pComment == null) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
        }

        // 判断回复的用户是否存在
        if (!StringTools.isEmpty(comment.getReplyUserId())) {
            UserInfo userInfo = userInfoService.getUserInfoByUserId(comment.getReplyUserId());
            if (userInfo == null) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
        }
        comment.setPostTime(new Date());
        if (null != image) {
            FileUploadDto fileUploadDto = fileUtils.uploadFile2Local(image, Constants.FILE_FOLDER_IMAGE, FileUploadTypeEnum.COMMENT_IMAGE);
            comment.setImgPath(fileUploadDto.getLocalPath());
        }


        Boolean needAudit = SysCacheUtils.getSysSetting().getAuditSetting().getCommentAudit();
        comment.setStatus(needAudit ? CommentStatusEnum.NO_AUDIT.getStatus() : CommentStatusEnum.AUDIT.getStatus());

        forumCommentMapper.insert(comment);

        if (needAudit) {
            return;
        }
        updateCommentInfo(comment, forumArticle, pComment);
    }


    public void updateCommentInfo(ForumComment comment, ForumArticle article, ForumComment pComment) throws BusinessException {
        Integer commentIntegral = SysCacheUtils.getSysSetting().getCommentSetting().getCommentIntegral();

        // 更改用户积分
        if (commentIntegral > 0) {
            userInfoService.updateUserIntegral(comment.getUserId(),
                    UserIntegralOperateTypeEnum.POST_COMMENT,
                    UserIntegralChangeTypeEnum.ADD.getChangeType(),
                    commentIntegral);
        }

        // 修改文章评论数
        if (comment.getPCommentId() == 0) {
            this.forumArticleMapper.updateArticleCount(UpdateArticleCountTypeEnum.COMMENT_COUNT.getType(), Constants.ONE, article.getArticleId());
        }

        // 记录消息
        UserMessage userMessage = new UserMessage();
        userMessage.setMessageType(MessageTypeEnum.COMMENT.getType());
        userMessage.setCreateTime(new Date());
        userMessage.setArticleId(article.getArticleId());
        userMessage.setCommentId(comment.getCommentId());
        userMessage.setSendUserId(comment.getUserId());
        userMessage.setSendNickName(comment.getNickName());
        userMessage.setStatus(MessageStatusEnum.NO_READ.getStatus());
        userMessage.setArticleTitle(article.getTitle());
        if (comment.getPCommentId() == 0) {
            userMessage.setReceivedUserId(article.getUserId());

            // 如果未传入replyUserId,则是对一级评论进行回复，replyUserId是被回复者的UserId
        } else if (comment.getPCommentId() != 0 && StringTools.isEmpty(comment.getReplyUserId())) {
            userMessage.setReceivedUserId(pComment.getUserId());

            // 如果传入了replyUserId,则是对二级评论进行回复
        } else if (comment.getPCommentId() != 0 && !StringTools.isEmpty(comment.getReplyUserId())) {
            userMessage.setReceivedUserId(comment.getReplyUserId());
        }
        if (!comment.getUserId().equals(userMessage.getReceivedUserId())) {
            userMessageService.add(userMessage);
        }
    }

    @Override
    public void delComment(String commentIds) throws BusinessException {
        String[] commentIdArray = commentIds.split(",");
        for (String commentId : commentIdArray) {
            forumCommentService.delCommentSingle(Integer.valueOf(commentId));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delCommentSingle(Integer commentId) throws BusinessException {
        ForumComment forumComment = forumCommentMapper.selectByCommentId(commentId);
        if (null == forumComment || forumComment.getStatus().equals(CommentStatusEnum.DEL.getStatus())) {
            return;
        }
        ForumComment comment = new ForumComment();
        comment.setStatus(CommentStatusEnum.DEL.getStatus());
        forumCommentMapper.updateByCommentId(comment, commentId);

        // 如果评论已审核更新文章评论数量
        if (CommentStatusEnum.AUDIT.getStatus().equals(forumComment.getStatus())) {
            if (forumComment.getPCommentId() == 0) {
                forumArticleMapper.updateArticleCount(UpdateArticleCountTypeEnum.COMMENT_COUNT.getType(), -Constants.ONE, forumComment.getArticleId());
            }
            Integer integral = SysCacheUtils.getSysSetting().getCommentSetting().getCommentIntegral();
            userInfoService.updateUserIntegral(forumComment.getUserId(), UserIntegralOperateTypeEnum.DEL_COMMENT, UserIntegralChangeTypeEnum.REDUCE.getChangeType(), integral);
        }

        // 发送系统消息
        UserMessage userMessage = new UserMessage();
        userMessage.setReceivedUserId(forumComment.getUserId());
        userMessage.setMessageType(MessageTypeEnum.SYS.getType());
        userMessage.setCreateTime(new Date());
        userMessage.setStatus(MessageStatusEnum.NO_READ.getStatus());
        userMessage.setMessageContent("评论【" + forumComment.getContent() + "】被管理员删除");
        userMessageService.add(userMessage);
    }

    @Override
    public void auditComment(String commentIds) throws BusinessException {
        String[] commentIdArray = commentIds.split(",");
        for (String commentId : commentIdArray) {
            forumCommentService.auditCommentSingle(Integer.valueOf(commentId));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditCommentSingle(Integer commentId) throws BusinessException {
        ForumComment forumComment = forumCommentMapper.selectByCommentId(commentId);
        if (null == forumComment || !forumComment.getStatus().equals(CommentStatusEnum.NO_AUDIT.getStatus())) {
            return;
        }
        ForumComment comment = new ForumComment();
        comment.setStatus(CommentStatusEnum.AUDIT.getStatus());
        forumCommentMapper.updateByCommentId(comment, commentId);

        ForumArticle forumArticle = forumArticleMapper.selectByArticleId(forumComment.getArticleId());
        ForumComment pComment = null;
        if (forumComment.getPCommentId()!=0 && StringTools.isEmpty(forumComment.getReplyUserId())) {
            pComment = forumCommentMapper.selectByCommentId(forumComment.getPCommentId());
        }
        updateCommentInfo(forumComment,forumArticle,pComment);
    }

}