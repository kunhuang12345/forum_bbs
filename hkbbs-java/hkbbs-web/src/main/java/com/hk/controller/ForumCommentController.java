package com.hk.controller;

import com.hk.annotation.GlobalInterceptor;
import com.hk.annotation.VerifyParam;
import com.hk.controller.base.ABaseController;
import com.hk.entity.constants.Constants;
import com.hk.entity.dto.SessionWebUserDto;
import com.hk.entity.dto.SysSettingDto;
import com.hk.entity.enums.ArticleStatusEnum;
import com.hk.entity.enums.PageSize;
import com.hk.entity.enums.ResponseCodeEnum;
import com.hk.entity.po.ForumComment;
import com.hk.entity.query.ForumCommentQuery;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.vo.ResponseVO;
import com.hk.exception.BusinessException;
import com.hk.service.ForumCommentService;
import com.hk.service.LikeRecordService;
import com.hk.utils.SysCacheUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;


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


}
