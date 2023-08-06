package com.hk.controller;

import com.hk.annotation.GlobalInterceptor;
import com.hk.annotation.VerifyParam;
import com.hk.controller.base.ABaseController;
import com.hk.entity.config.WebConfig;
import com.hk.entity.constants.Constants;
import com.hk.entity.dto.SessionWebUserDto;
import com.hk.entity.enums.ArticleOrderTypeEnum;
import com.hk.entity.enums.ArticleStatusEnum;
import com.hk.entity.enums.OperateRecordOpTypeEnum;
import com.hk.entity.enums.ResponseCodeEnum;
import com.hk.entity.po.*;
import com.hk.entity.query.ForumArticleAttachmentQuery;
import com.hk.entity.query.ForumArticleQuery;
import com.hk.entity.vo.*;
import com.hk.entity.vo.web.ForumArticleAttachmentVO;
import com.hk.entity.vo.web.ForumArticleDetailVO;
import com.hk.entity.vo.web.ForumArticleVO;
import com.hk.entity.vo.web.UserDownloadInfoVO;
import com.hk.exception.BusinessException;
import com.hk.service.*;
import com.hk.utils.CopyUtils;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequestMapping("/forum")
@RestController
public class ForumArticleController extends ABaseController {

    private Logger logger = LoggerFactory.getLogger(ForumArticleController.class);

    @Resource
    private ForumArticleService forumArticleService;

    @Resource
    private ForumArticleAttachmentService articleAttachmentService;

    @Resource
    private LikeRecordService likeRecordService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private ForumArticleAttachmentDownloadService articleAttachmentDownloadService;

    @Resource
    private WebConfig webConfig;

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

    @RequestMapping("/getUserDownloadInfo")
    @GlobalInterceptor(checkLogin = true,checkParams = true)
    public ResponseVO getUserDownloadInfo(HttpSession session, @VerifyParam(required = true) String fileId) throws BusinessException {
        SessionWebUserDto userInfoFromSession = getUserInfoFromSession(session);

        UserInfo userInfo = userInfoService.getUserInfoByUserId(userInfoFromSession.getUserId());

        UserDownloadInfoVO downloadInfoVO = new UserDownloadInfoVO();
        downloadInfoVO.setUserIntegral(userInfo.getCurrentIntegral());

        ForumArticleAttachmentDownload articleAttachmentDownload = articleAttachmentDownloadService.getForumArticleAttachmentDownloadByFileIdAndUserId(fileId,
                userInfoFromSession.getUserId());

        if (articleAttachmentDownload != null) {
            downloadInfoVO.setHaveDownload(true);
        }

        return getSuccessResponseVO(downloadInfoVO);
    }

    @RequestMapping("/attachmentDownload")
    @GlobalInterceptor(checkParams = true,checkLogin = true)
    public void attachmentDownload(HttpSession session, HttpServletRequest request , HttpServletResponse response,
                                   @VerifyParam(required = true) String fileId) throws BusinessException {
        ForumArticleAttachment attachment = articleAttachmentService.downloadAttachment(fileId, getUserInfoFromSession(session));
        InputStream in = null;
        OutputStream out = null;
        String downloadFileName = attachment.getFileName();
        String filePath = webConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_ATTACHMENT + attachment.getFilePath();
        File file = new File(filePath);
        try {
            in = new FileInputStream(file);
            out = response.getOutputStream();
            response.setContentType("application/x-msdownload; charset=UTF-8");
            // 解决中文乱码问题
            if (request.getHeader("User-Agent").toLowerCase().indexOf("mise") > 0) { //IE浏览器
                downloadFileName = URLEncoder.encode(downloadFileName, StandardCharsets.UTF_8);
            } else {
                downloadFileName = new String(downloadFileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
            }
            response.setHeader("content-Disposition","attachment;filename=\"" + downloadFileName + "\"");
            byte[] byteData = new byte[1024];
            int len = 0;
            while ((len = in.read(byteData)) != -1) {
                out.write(byteData,0,len);
            }
            out.flush();
        } catch (Exception e) {
            logger.error("下载异常",e);
            throw new BusinessException("下载失败");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("IO异常",e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("IO异常",e);
                }
            }
        }
    }


}
