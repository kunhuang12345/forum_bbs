package com.hk.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.hk.entity.po.ForumArticle;
import com.hk.entity.query.ForumArticleQuery;
import com.hk.exception.BusinessException;
import com.hk.mapper.ForumArticleMapper;
import com.hk.service.ForumBoardService;
import com.hk.entity.po.ForumBoard;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.ForumBoardQuery;
import com.hk.mapper.ForumBoardMapper;
import com.hk.entity.query.SimplePage;
import com.hk.entity.enums.PageSize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @Description:文章板块信息ServiceImpl
 * @author:AUTHOR
 * @date:2023/07/29
 */
@Service("forumBoardService")
public class ForumBoardServiceImpl implements ForumBoardService {

	@Resource
	private ForumArticleMapper<ForumArticle, ForumArticleQuery> forumArticleMapper;

	@Resource
	private ForumBoardMapper<ForumBoard, ForumBoardQuery> forumBoardMapper;

	/**
	 * 根据条件查询列表
	 */
	public List<ForumBoard> findListByParam(ForumBoardQuery query) {
		return this.forumBoardMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(ForumBoardQuery query) {
		return this.forumBoardMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<ForumBoard> findListByPage(ForumBoardQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<ForumBoard> list = this.findListByParam(query);
		PaginationResultVO<ForumBoard> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(ForumBoard bean) {
		return this.forumBoardMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<ForumBoard> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.forumBoardMapper.insertBatch(beanList);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<ForumBoard> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.forumBoardMapper.insertOrUpdateBatch(beanList);
	}

	/**
	 * 根据BoardId查询
	 */
	public ForumBoard getForumBoardByBoardId(Integer boardId) {
		return this.forumBoardMapper.selectByBoardId(boardId);
	}

	/**
	 * 根据BoardId更新
	 */
	public Integer updateForumBoardByBoardId(ForumBoard forumBoard, Integer boardId) {
		return this.forumBoardMapper.updateByBoardId(forumBoard, boardId);
	}

	/**
	 * 根据BoardId删除
	 */
	public Integer deleteForumBoardByBoardId(Integer boardId) {
		return this.forumBoardMapper.deleteByBoardId(boardId);
	}

    @Override
    public List<ForumBoard> getBordTree(Integer postType) {
		ForumBoardQuery boardQuery = new ForumBoardQuery();
		boardQuery.setOrderBy("sort asc");
		boardQuery.setPostType(postType);
		List<ForumBoard> forumBoards = forumBoardMapper.selectList(boardQuery);
		return convertLine2Tree(forumBoards,0);
	}

    @Override
    public void saveForumBoard(ForumBoard forumBoard) throws BusinessException {
        if (forumBoard.getBoardId() == null) {
			ForumBoardQuery forumBoardQuery = new ForumBoardQuery();
			forumBoardQuery.setBoardId(forumBoard.getPBoardId());

			Integer count = forumBoardMapper.selectCount(forumBoardQuery);
			forumBoard.setSort(count + 1);
			forumBoardMapper.insert(forumBoard);
		} else {
			ForumBoard dbInfo = forumBoardMapper.selectByBoardId(forumBoard.getBoardId());
			if (dbInfo == null) {
				throw new BusinessException("板块信息不存在");
			}
			forumBoardMapper.updateByBoardId(forumBoard,forumBoard.getBoardId());
			if (!dbInfo.getBoardName().equals(forumBoard.getBoardName())) {
				forumArticleMapper.updateBoardNameBatch(dbInfo.getPBoardId()==0?0:1, forumBoard.getBoardName(), forumBoard.getBoardId());
			}
		}
    }

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void changeSort(String boardIds) {
		String[] boardIdArray = boardIds.split(",");
		Integer index = 1;
		for (String boardIdStr : boardIdArray) {
			Integer boardId = Integer.valueOf(boardIdStr);
			ForumBoard board = new ForumBoard();
			board.setSort(index);
			forumBoardMapper.updateByBoardId(board,boardId);
			index++;
		}
	}

	private List<ForumBoard> convertLine2Tree(List<ForumBoard> dataList,Integer pid) {
		List<ForumBoard> children = new ArrayList<>();
		for (ForumBoard m: dataList) {
			if (m.getPBoardId().equals(pid)) {
				m.setChildren(convertLine2Tree(dataList,m.getBoardId()));
				children.add(m);
			}
		}
		return children;
	}

}