package com.hk.controller;

import com.hk.controller.base.ABaseController;
import com.hk.entity.vo.ResponseVO;
import com.hk.service.ForumBoardService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/board")
public class ForumBoardController extends ABaseController {
    @Resource
    private ForumBoardService forumBoardService;

    @RequestMapping("/loadBoard")
    public ResponseVO loadBoard() {
        return getSuccessResponseVO(forumBoardService.getBordTree(null));
    }
}
