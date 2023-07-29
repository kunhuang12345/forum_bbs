package com.hk.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @Description:邮箱验证码Mapper
 * @author:AUTHOR
 * @date:2023/07/29
 */
public interface EmailCodeMapper<T, P> extends BaseMapper<T, P> {

	/**
	 * 根据EmailAndCode查询
	 */
	T selectByEmailAndCode(@Param("email") String email, @Param("code") String code);

	/**
	 * 根据EmailAndCode更新
	 */
	Integer updateByEmailAndCode(@Param("bean") T t,@Param("email") String email, @Param("code") String code);

	/**
	 * 根据EmailAndCode删除
	 */
	Integer deleteByEmailAndCode(@Param("email") String email, @Param("code") String code);

	void disableEmailCode(@Param("email") String email);


}