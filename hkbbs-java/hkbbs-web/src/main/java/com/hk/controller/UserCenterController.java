package com.hk.controller;

import com.hk.annotation.GlobalInterceptor;
import com.hk.annotation.VerifyParam;
import com.hk.controller.base.ABaseController;
import com.hk.entity.constants.Constants;
import com.hk.entity.dto.SessionWebUserDto;
import com.hk.entity.dto.UserMessageDto;
import com.hk.entity.enums.ArticleStatusEnum;
import com.hk.entity.enums.MessageTypeEnum;
import com.hk.entity.enums.ResponseCodeEnum;
import com.hk.entity.enums.UserStatusEnum;
import com.hk.entity.po.ForumArticle;
import com.hk.entity.po.UserInfo;
import com.hk.entity.po.UserIntegralRecord;
import com.hk.entity.po.UserMessage;
import com.hk.entity.query.ForumArticleQuery;
import com.hk.entity.query.LikeRecordQuery;
import com.hk.entity.query.UserIntegralRecordQuery;
import com.hk.entity.query.UserMessageQuery;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.vo.ResponseVO;
import com.hk.entity.vo.web.ForumArticleVO;
import com.hk.entity.vo.web.UserInfoVO;
import com.hk.exception.BusinessException;
import com.hk.service.*;
import com.hk.utils.CopyUtils;
import org.apache.tomcat.util.bcel.classfile.Constant;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController("userCenterController")
@RequestMapping("/uCenter")
public class UserCenterController extends ABaseController {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private ForumArticleService forumArticleService;

    @Resource
    private LikeRecordService likeRecordService;

    @Resource
    private UserIntegralRecordService userIntegralRecordService;

    @Resource
    private UserMessageService userMessageService;

    /**
     * 获取用户信息
     * @param userId
     * @return
     * @throws BusinessException
     */
    @RequestMapping("/getUserInfo")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO getUserInfo(@VerifyParam(required = true) String userId) throws BusinessException {
        UserInfo userInfo = userInfoService.getUserInfoByUserId(userId);
        // 用户不存在或者用户状态为禁止
        if (null == userInfo || userInfo.getStatus().equals(UserStatusEnum.DISABLE.getStatus())) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
        ForumArticleQuery articleQuery = new ForumArticleQuery();
        articleQuery.setUserId(userId);
        articleQuery.setStatus(ArticleStatusEnum.AUDIT.getStatus());
        Integer postCount = forumArticleService.findCountByParam(articleQuery);

        UserInfoVO userInfoVO = CopyUtils.copy(userInfo, UserInfoVO.class);
        userInfoVO.setPostCount(postCount);
        LikeRecordQuery recordQuery = new LikeRecordQuery();
        recordQuery.setAuthorUserId(userId);
        Integer likeCount = likeRecordService.findCountByParam(recordQuery);

        userInfoVO.setLikeCount(likeCount);
        return getSuccessResponseVO(userInfoVO);
    }

    @RequestMapping("/loadUserArticle")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO loadUserArticle(HttpSession session,
                                      @VerifyParam(required = true) String userId,
                                      @VerifyParam(required = true) Integer type,
                                      Integer pageNo) throws BusinessException {
        UserInfo userInfo = userInfoService.getUserInfoByUserId(userId);
        if (null == userInfo || userInfo.getStatus().equals(UserStatusEnum.DISABLE.getStatus())) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
        ForumArticleQuery articleQuery = new ForumArticleQuery();
        articleQuery.setOrderBy("post_time desc");
        articleQuery.setPageNo(pageNo);

        if (type.equals(Constants.ZERO)) {
            // 加载自己的文章
            articleQuery.setUserId(userId);
        } else if (type.equals(Constants.ONE)) {
            // 加载评论过的文章
            articleQuery.setCommentUserId(userId);
        } else if (type.equals(2)) {
            // 加载点赞过的文章
            articleQuery.setLikeUserId(userId);
        }

        SessionWebUserDto userDto = getUserInfoFromSession(session);
        if (userDto != null) {
            articleQuery.setCurrentUserId(userDto.getUserId());
        } else {
            // 未登录则只加载已审核的文章
            articleQuery.setStatus(ArticleStatusEnum.AUDIT.getStatus());
        }
        PaginationResultVO<ForumArticle> listByPage = forumArticleService.findListByPage(articleQuery);

        return getSuccessResponseVO(convert2PaginationVO(listByPage, ForumArticleVO.class));
    }

    @RequestMapping("/updateUserInfo")
    @GlobalInterceptor(checkParams = true, checkLogin = true)
    public ResponseVO updateUserInfo(HttpSession session,
                                     @VerifyParam(max = 100) String personDescription,
                                     MultipartFile avatar,
                                     Integer sex) throws BusinessException {

        SessionWebUserDto userDto = getUserInfoFromSession(session);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userDto.getUserId());
        userInfo.setSex(sex);
        userInfo.setPersonDescription(personDescription);
        userInfoService.updateUserInfo(userInfo, avatar);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadUserIntegralRecord")
    @GlobalInterceptor(checkParams = true, checkLogin = true) // 只能本人访问
    public ResponseVO loadUserIntegralRecord(HttpSession session,
                                             Integer pageNo,
                                             String createTimeStart,
                                             String createTimeEnd) throws BusinessException {

        UserIntegralRecordQuery recordQuery = new UserIntegralRecordQuery();
        recordQuery.setUserId(getUserInfoFromSession(session).getUserId());
        recordQuery.setPageNo(pageNo);
        recordQuery.setCreateTimeStart(createTimeStart);
        recordQuery.setCreateTimeEnd(createTimeEnd);
        recordQuery.setOrderBy("record_id desc");
        PaginationResultVO<UserIntegralRecord> listByPage = userIntegralRecordService.findListByPage(recordQuery);
        return getSuccessResponseVO(listByPage);
    }

    // 获取消息数
    @RequestMapping("/getMessageCount")
    @GlobalInterceptor(checkParams = true, checkLogin = true) // 只能本人访问
    public ResponseVO getMessageCount(HttpSession session) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        UserMessageDto userMessageCount = userMessageService.getUserMessageCount(userDto.getUserId());
        return getSuccessResponseVO(userMessageCount);
    }

    @RequestMapping("/loadMessageList")
    @GlobalInterceptor(checkParams = true, checkLogin = true) // 只能本人访问
    public ResponseVO loadMessageList(HttpSession session,@VerifyParam String code, Integer pageNo) throws BusinessException {
        MessageTypeEnum messageType = MessageTypeEnum.getByCode(code);
        if (messageType == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        SessionWebUserDto userDto = getUserInfoFromSession(session);
        UserMessageQuery query = new UserMessageQuery();
        query.setPageNo(pageNo);
        query.setReceivedUserId(userDto.getUserId());
        query.setMessageType(messageType.getType());
        query.setOrderBy("message_id desc");

        PaginationResultVO<UserMessage> listByPage = userMessageService.findListByPage(query);

        boolean equal = pageNo == null || pageNo == 1 || pageNo == 0;
        if (equal) {
            userMessageService.readMessageByType(userDto.getUserId(),messageType.getType());
        }

        return getSuccessResponseVO(listByPage);
    }
}
