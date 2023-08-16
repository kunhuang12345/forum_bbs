package com.hk.controller;

import com.hk.annotation.GlobalInterceptor;
import com.hk.annotation.VerifyParam;
import com.hk.controller.base.ABaseController;
import com.hk.entity.constants.Constants;
import com.hk.entity.dto.SessionWebUserDto;
import com.hk.entity.dto.SysSettingDto;
import com.hk.entity.enums.*;
import com.hk.entity.po.ForumComment;
import com.hk.entity.po.LikeRecord;
import com.hk.entity.query.ForumCommentQuery;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.vo.ResponseVO;
import com.hk.exception.BusinessException;
import com.hk.service.ForumCommentService;
import com.hk.service.LikeRecordService;
import com.hk.utils.StringTools;
import com.hk.utils.SysCacheUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;


@RequestMapping("/comment")
@RestController
public class ForumCommentController extends ABaseController {

    @Resource
    private ForumCommentService forumCommentService;

    @Resource
    private LikeRecordService likeRecordService;

    @RequestMapping("/loadComment")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO loadComment(HttpSession session,
                                  @VerifyParam(required = true) String articleId,
                                  Integer pageNo,
                                  Integer orderType) throws BusinessException {
        final String ORDER_TYPE0 = "good_count desc,comment_id asc";
        final String ORDER_TYPE1 = "comment_id desc";
        if (!SysCacheUtils.getSysSetting().getCommentSetting().getCommentOpen()) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 为分页查询塞值
        ForumCommentQuery forumCommentQuery = new ForumCommentQuery();
        forumCommentQuery.setArticleId(articleId);
        String orderBy = orderType == null || orderType.equals(Constants.ZERO) ? ORDER_TYPE0 : ORDER_TYPE1;
        forumCommentQuery.setOrderBy("top_type desc," + orderBy);

        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        if (sessionWebUserDto != null) {
            forumCommentQuery.setQueryLikeType(true);
            forumCommentQuery.setCurrentUserId(sessionWebUserDto.getUserId());
        } else {
            forumCommentQuery.setStatus(ArticleStatusEnum.AUDIT.getStatus());
        }
        forumCommentQuery.setPageNo(pageNo);
        forumCommentQuery.setPageSize(PageSize.SIZE50.getSize());
        forumCommentQuery.setPCommentId(Constants.ZERO);
        forumCommentQuery.setLoadChildren(true);
        PaginationResultVO<ForumComment> listByPage = forumCommentService.findListByPage(forumCommentQuery);
        return getSuccessResponseVO(listByPage);
    }

    @RequestMapping("/doLike")
    @GlobalInterceptor(checkParams = true, checkLogin = true)
    public ResponseVO doLike(HttpSession session,
                             @VerifyParam(required = true) Integer commentId) throws BusinessException {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        String objectId = String.valueOf(commentId);
        likeRecordService.doLike(objectId, userDto.getUserId(), userDto.getNickName(), OperateRecordOpTypeEnum.COMMENT_LIKE);

        LikeRecord likeRecord = likeRecordService.getLikeRecordByObjectIdAndUserIdAndOpType(objectId, userDto.getUserId(), OperateRecordOpTypeEnum.COMMENT_LIKE.getType());

        ForumComment forumComment = forumCommentService.getForumCommentByCommentId(commentId);

        forumComment.setLikeType(likeRecord == null ? Constants.ZERO : Constants.ONE);

        return getSuccessResponseVO(forumComment);
    }

    @RequestMapping("/changeTopType")
    @GlobalInterceptor(checkParams = true, checkLogin = true)
    public ResponseVO changeTopType(HttpSession session,
                                    @VerifyParam(required = true) Integer commentId,
                                    @VerifyParam(required = true) Integer topType) throws BusinessException {
        forumCommentService.changeTopType(getUserInfoFromSession(session).getUserId(), commentId, topType);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/postComment")
    @GlobalInterceptor(checkParams = true, checkLogin = true, frequencyType = UserOperatefrequencyTypeEnum.POST_COMMENT)
    public ResponseVO postComment(HttpSession session,
                                  @VerifyParam(required = true) String articleId,
                                  @VerifyParam(required = true) Integer pCommentId,
                                  @VerifyParam(min = 2, max = 800) String content,
                                  MultipartFile image,
                                  String replyUserId
    ) throws BusinessException {
        if (!SysCacheUtils.getSysSetting().getCommentSetting().getCommentOpen()) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (StringTools.isEmpty(content) && image == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        SessionWebUserDto userDto = getUserInfoFromSession(session);
        content = StringTools.escapeHtml(content);

        ForumComment comment = new ForumComment();

        // 塞入评论基本信息
        comment.setUserId(userDto.getUserId());
        comment.setNickName(userDto.getNickName());
        comment.setUserIpAddress(userDto.getProvince());
        comment.setPCommentId(pCommentId);
        comment.setArticleId(articleId);
        comment.setContent(content);
        comment.setReplyUserId(replyUserId);
        comment.setTopType(CommentTopTypeEnum.NO_TOP.getType());
        forumCommentService.postComment(comment, image);
        if (pCommentId != 0) {
            ForumCommentQuery forumCommentQuery = new ForumCommentQuery();
            forumCommentQuery.setArticleId(articleId);
            forumCommentQuery.setPCommentId(pCommentId);
            forumCommentQuery.setOrderBy("comment_id desc");
            forumCommentQuery.setStatus(Constants.ONE);
            List<ForumComment> children = forumCommentService.findListByParam(forumCommentQuery);
            return getSuccessResponseVO(children);
        }
        return getSuccessResponseVO(comment);
    }

}
