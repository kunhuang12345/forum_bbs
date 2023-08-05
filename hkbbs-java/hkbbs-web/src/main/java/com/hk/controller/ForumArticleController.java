package com.hk.controller;

import com.hk.annotation.GlobalInterceptor;
import com.hk.annotation.VerifyParam;
import com.hk.controller.base.ABaseController;
import com.hk.entity.dto.SessionWebUserDto;
import com.hk.entity.enums.ArticleOrderTypeEnum;
import com.hk.entity.enums.ArticleStatusEnum;
import com.hk.entity.query.ForumArticleQuery;
import com.hk.entity.vo.ForumArticleVO;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.vo.ResponseVO;
import com.hk.service.ForumArticleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RequestMapping("/forum")
@RestController
public class ForumArticleController extends ABaseController {

    @Resource
    private ForumArticleService forumArticleService;

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
    public ResponseVO getArticleDetail(HttpSession session, @VerifyParam(required = true) String articleId) {

        return getSuccessResponseVO(null);
    }
}
