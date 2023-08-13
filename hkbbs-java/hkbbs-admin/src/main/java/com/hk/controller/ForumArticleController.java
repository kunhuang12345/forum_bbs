package com.hk.controller;

import com.hk.annotation.GlobalInterceptor;
import com.hk.annotation.VerifyParam;
import com.hk.controller.base.ABaseController;
import com.hk.entity.config.AdminConfig;
import com.hk.entity.constants.Constants;
import com.hk.entity.po.ForumArticle;
import com.hk.entity.po.ForumArticleAttachment;
import com.hk.entity.query.ForumArticleAttachmentQuery;
import com.hk.entity.query.ForumArticleQuery;
import com.hk.entity.query.ForumCommentQuery;
import com.hk.entity.vo.ResponseVO;
import com.hk.exception.BusinessException;
import com.hk.service.ForumArticleAttachmentService;
import com.hk.service.ForumArticleService;
import com.hk.service.ForumCommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.stream.events.Comment;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequestMapping("/forum")
@RestController
public class ForumArticleController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(ForumArticleController.class);

    @Resource
    private ForumArticleService forumArticleService;

    @Resource
    private ForumArticleAttachmentService articleAttachmentService;

    @Resource
    private AdminConfig adminConfig;

    @Resource
    private ForumCommentService forumCommentService;

    @RequestMapping("/loadArticle")
    public ResponseVO loadArticle(ForumArticleQuery forumArticleQuery) {

        forumArticleQuery.setOrderBy("post_time desc");
        return getSuccessResponseVO(forumArticleService.findListByPage(forumArticleQuery));

    }

    @RequestMapping("/delArticle")
    public ResponseVO delArticle(@VerifyParam String articleIds) throws BusinessException {
        forumArticleService.delArticle(articleIds);

        return getSuccessResponseVO(null);

    }

    /**
     * 修改用户文章板块
     */
    @RequestMapping("/updateBoard")
    public ResponseVO updateBoard(@VerifyParam(required = true) String articleId,
                                  @VerifyParam(required = true) Integer pBoardId,
                                  Integer boardId) throws BusinessException {
        boardId = boardId == null ? 0 : boardId;
        forumArticleService.updateBoard(articleId, pBoardId, boardId);
        return getSuccessResponseVO(null);
    }

    /**
     * 获取附件
     */
    @RequestMapping("/getAttachment")
    public ResponseVO getAttachment(@VerifyParam(required = true) String articleId) throws BusinessException {

        ForumArticleAttachmentQuery forumArticleAttachmentQuery = new ForumArticleAttachmentQuery();
        forumArticleAttachmentQuery.setArticleId(articleId);
        List<ForumArticleAttachment> listByParam = articleAttachmentService.findListByParam(forumArticleAttachmentQuery);
        if (listByParam.isEmpty()) {
            throw new BusinessException("附件不存在");
        }

        return getSuccessResponseVO(listByParam.get(0));
    }

    /**
     * 下载附件
     */
    @RequestMapping("/attachmentDownload")
    @GlobalInterceptor(checkParams = true, checkLogin = true)
    public void attachmentDownload(HttpSession session, HttpServletRequest request, HttpServletResponse response,
                                   @VerifyParam(required = true) String fileId) throws BusinessException {
        ForumArticleAttachment attachment = articleAttachmentService.downloadAttachment(fileId, getUserInfoFromSession(session));
        InputStream in = null;
        OutputStream out = null;
        String downloadFileName = attachment.getFileName();
        String filePath = adminConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_ATTACHMENT + attachment.getFilePath();
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
            response.setHeader("content-Disposition", "attachment;filename=\"" + downloadFileName + "\"");
            byte[] byteData = new byte[1024];
            int len = 0;
            while ((len = in.read(byteData)) != -1) {
                out.write(byteData, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            logger.error("下载异常", e);
            throw new BusinessException("下载失败");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("IO异常", e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("IO异常", e);
                }
            }
        }
    }

    /**
     * 文章置顶
     */
    @RequestMapping("/topArticle")
    public ResponseVO topArticle(@VerifyParam(required = true) String articleId, Integer topType) throws BusinessException {
        ForumArticle forumArticle = new ForumArticle();
        forumArticle.setTopType(topType);
        forumArticleService.updateForumArticleByArticleId(forumArticle, articleId);
        return getSuccessResponseVO(null);
    }

    /**
     * 文章审核
     */
    @RequestMapping("/auditArticle")
    public ResponseVO auditArticle(@VerifyParam String articleIds) throws BusinessException {
        forumArticleService.auditArticle(articleIds);

        return getSuccessResponseVO(null);

    }

    @RequestMapping("/loadComment")
    public ResponseVO loadComment(ForumCommentQuery forumCommentQuery) throws BusinessException {
        forumCommentQuery.setLoadChildren(true);
        forumCommentQuery.setOrderBy("post_time desc");
        return getSuccessResponseVO(forumCommentService.findListByPage(forumCommentQuery));
    }

    @RequestMapping("/loadComment4Article")
    public ResponseVO loadComment4Article(ForumCommentQuery forumCommentQuery) throws BusinessException {

        forumCommentQuery.setLoadChildren(true);
        forumCommentQuery.setOrderBy("post_time desc");
        forumCommentQuery.setPCommentId(0);
        return getSuccessResponseVO(forumCommentService.findListByPage(forumCommentQuery));
    }

    @RequestMapping("/delComment")
    public ResponseVO delComment(@VerifyParam(required = true) String commentIds) throws BusinessException {
        forumCommentService.delComment(commentIds);
        return getSuccessResponseVO(null);

    }

}
