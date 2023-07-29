package com.hk.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @Description:用户附件下载Mapper
 * @author:AUTHOR
 * @date:2023/07/29
 */
public interface ForumArticleAttachmentDownloadMapper<T, P> extends BaseMapper<T, P> {

	/**
	 * 根据FileIdAndUserId查询
	 */
	T selectByFileIdAndUserId(@Param("file_id") String fileId, @Param("user_id") String userId);

	/**
	 * 根据FileIdAndUserId更新
	 */
	Integer updateByFileIdAndUserId(@Param("bean") T t,@Param("file_id") String fileId, @Param("user_id") String userId);

	/**
	 * 根据FileIdAndUserId删除
	 */
	Integer deleteByFileIdAndUserId(@Param("file_id") String fileId, @Param("user_id") String userId);


}