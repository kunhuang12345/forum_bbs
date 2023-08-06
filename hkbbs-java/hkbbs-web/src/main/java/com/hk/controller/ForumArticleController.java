package com.hk.controller;

import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.hk.annotation.GlobalInterceptor;
import com.hk.annotation.VerifyParam;
import com.hk.controller.base.ABaseController;
import com.hk.entity.constants.Constants;
import com.hk.entity.dto.SessionWebUserDto;
import com.hk.entity.enums.ArticleOrderTypeEnum;
import com.hk.entity.enums.ArticleStatusEnum;
import com.hk.entity.enums.OperateRecordOpTypeEnum;
import com.hk.entity.enums.ResponseCodeEnum;
import com.hk.entity.po.ForumArticle;
import com.hk.entity.po.ForumArticleAttachment;
import com.hk.entity.po.LikeRecord;
import com.hk.entity.query.ForumArticleAttachmentQuery;
import com.hk.entity.query.ForumArticleQuery;
import com.hk.entity.vo.*;
import com.hk.exception.BusinessException;
import com.hk.service.ForumArticleAttachmentService;
import com.hk.service.ForumArticleService;
import com.hk.service.LikeRecordService;
import com.hk.utils.CopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@RequestMapping("/forum")
@RestController
public class ForumArticleController extends ABaseController {

    @Resource
    private ForumArticleService forumArticleService;

    @Resource
    private ForumArticleAttachmentService articleAttachmentService;

    @Resource
    private LikeRecordService likeRecordService;

    @RequestMapping("/loadArticle")
    public ResponseVO loadArticle(HttpSession session, Integer boardId, Integer pBoardId, Integer orderType, Integer pageNo) {
        ForumArticleQuery articleQuery = new ForumArticleQuery();
        articleQuery.setBoardId(boardId == null || boardId == 0 ? null : boardId);
        articleQuery.setPBoardId(pBoardId);
        articleQuery.setPageNo(pageNo);
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        if (sessionWebUserDto != null) {
            articleQuery.setCurrentUserId(sessionWebUserDto.getUserId());
        } else {
            articleQuery.setStatus(ArticleStatusEnum.AUDIT.getStatus());
        }

        ArticleOrderTypeEnum orderTypeEnum = ArticleOrderTypeEnum.getByType(orderType);
        articleQuery.setOrderBy(orderTypeEnum == null ? ArticleOrderTypeEnum.HOT.getOrderSql() : orderTypeEnum.getOrderSql());
        PaginationResultVO resultVO = forumArticleService.findListByPage(articleQuery);
        return getSuccessResponseVO(convert2PaginationVO(resultVO, ForumArticleVO.class));
    }

    @RequestMapping("/getArticleDetail")
    @GlobalInterceptor
    public ResponseVO getArticleDetail(HttpSession session, @VerifyParam(required = true) String articleId) throws BusinessException {
        SessionWebUserDto userInfoFromSession = getUserInfoFromSession(session);
        ForumArticle forumArticle = forumArticleService.readArticle(articleId);

        if (ArticleStatusEnum.AUDIT.getStatus().equals(forumArticle.getStatus()) ||
                (ArticleStatusEnum.NO_AUDIT.getStatus().equals(forumArticle.getStatus()) &&
                        (userInfoFromSession != null &&
                                (userInfoFromSession.getUserId().equals(forumArticle.getUserId()) || userInfoFromSession.getAdmin())))
        ) {
            ForumArticleDetailVO articleDetailVO = new ForumArticleDetailVO();
            articleDetailVO.setForumArticle(CopyUtils.copy(forumArticle,ForumArticleVO.class));

            // 有附件
            if (forumArticle.getAttachmentType().equals(Constants.ONE)) {
                ForumArticleAttachmentQuery attachmentQuery = new ForumArticleAttachmentQuery();
                attachmentQuery.setArticleId(articleId);
                List<ForumArticleAttachment> attachmentList = articleAttachmentService.findListByParam(attachmentQuery);
                if (!attachmentList.isEmpty()) {
                    articleDetailVO.setAttachment(CopyUtils.copy(attachmentList.get(0), ForumArticleAttachmentVO.class));
                }
            }
            // 是否已点赞
            if (userInfoFromSession != null) {
                LikeRecord record = likeRecordService.getLikeRecordByObjectIdAndUserIdAndOpType(articleId, userInfoFromSession.getUserId(), OperateRecordOpTypeEnum.ARTICLE_LIKE.getType());
                if (record != null) {
                    articleDetailVO.setHaveLike(true);
                }
            }


            return getSuccessResponseVO(articleDetailVO);
        } else {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
    }

    @RequestMapping("/doLike")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO doLike(HttpSession session, @VerifyParam(required = true) String articleId) throws BusinessException {
        SessionWebUserDto userInfoFromSession = getUserInfoFromSession(session);
        likeRecordService.doLike(articleId,userInfoFromSession.getUserId(),userInfoFromSession.getNickName(),OperateRecordOpTypeEnum.ARTICLE_LIKE);
        return getSuccessResponseVO("已点赞");
    }
}
