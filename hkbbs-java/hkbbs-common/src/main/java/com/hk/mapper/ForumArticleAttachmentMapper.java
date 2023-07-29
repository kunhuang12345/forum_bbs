package com.hk.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @Description:文件信息Mapper
 * @author:AUTHOR
 * @date:2023/07/29
 */
public interface ForumArticleAttachmentMapper<T, P> extends BaseMapper<T, P> {

	/**
	 * 根据FileId查询
	 */
	T selectByFileId(@Param("file_id") String fileId);

	/**
	 * 根据FileId更新
	 */
	Integer updateByFileId(@Param("bean") T t,@Param("file_id") String fileId);

	/**
	 * 根据FileId删除
	 */
	Integer deleteByFileId(@Param("file_id") String fileId);


}