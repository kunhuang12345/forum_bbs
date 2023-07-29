package com.hk.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @Description:文章信息Mapper
 * @author:AUTHOR
 * @date:2023/07/29
 */
public interface ForumArticleMapper<T, P> extends BaseMapper<T, P> {

	/**
	 * 根据ArticleId查询
	 */
	T selectByArticleId(@Param("article_id") String articleId);

	/**
	 * 根据ArticleId更新
	 */
	Integer updateByArticleId(@Param("bean") T t,@Param("article_id") String articleId);

	/**
	 * 根据ArticleId删除
	 */
	Integer deleteByArticleId(@Param("article_id") String articleId);


}