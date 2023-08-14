package com.hk.controller;

import com.hk.annotation.GlobalInterceptor;
import com.hk.annotation.VerifyParam;
import com.hk.controller.base.ABaseController;
import com.hk.entity.constants.Constants;
import com.hk.entity.dto.FileUploadDto;
import com.hk.entity.enums.FileUploadTypeEnum;
import com.hk.entity.po.ForumBoard;
import com.hk.entity.vo.ResponseVO;
import com.hk.exception.BusinessException;
import com.hk.service.ForumBoardService;
import com.hk.utils.FileUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/board")
public class ForumBoardController extends ABaseController {

    @Resource
    private ForumBoardService forumBoardService;

    @Resource
    private FileUtils fileUtils;

    @RequestMapping("/loadBoard")
    public ResponseVO loadBoard(){
        return getSuccessResponseVO(forumBoardService.getBordTree(null));
    }


    /**
     * 保存或者新增板块
     * */
    @RequestMapping("/saveBoard")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO saveBoard(Integer boardId,
                                @VerifyParam(required = true) Integer pBoardId,
                                @VerifyParam(required = true) String boardName,
                                String boardDesc,
                                Integer postType,
                                MultipartFile cover) throws BusinessException {
        ForumBoard forumBoard = new ForumBoard();
        forumBoard.setBoardId(boardId);
        forumBoard.setPBoardId(pBoardId);
        forumBoard.setBoardName(boardName);
        forumBoard.setBoardDesc(boardDesc);
        forumBoard.setPostType(postType);

        if (cover != null) {
            FileUploadDto fileUploadDto = fileUtils.uploadFile2Local(cover, Constants.FILE_FOLDER_IMAGE, FileUploadTypeEnum.ARTICLE_COVER);
            forumBoard.setCover(fileUploadDto.getLocalPath());
        }

        forumBoardService.saveForumBoard(forumBoard);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/delBoard")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO delBoard(@VerifyParam(required = true) Integer boardId) {
        forumBoardService.deleteForumBoardByBoardId(boardId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/changeBoardSort")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO changeBoardSort(@VerifyParam(required = true) String boardIds) {
        forumBoardService.changeSort(boardIds);
        return getSuccessResponseVO(null);
    }

}
