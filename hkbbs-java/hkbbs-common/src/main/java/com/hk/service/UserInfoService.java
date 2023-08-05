package com.hk.service;

import java.util.List;
import java.util.Date;

import com.hk.entity.dto.SessionWebUserDto;
import com.hk.entity.enums.UserIntegralOperateTypeEnum;
import com.hk.exception.BusinessException;
import com.hk.utils.DateUtils;
import com.hk.entity.enums.DateTimePatternEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.hk.entity.po.UserInfo;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.UserInfoQuery;

/**
 * @Description:用户信息Service
 * @author:AUTHOR
 * @date:2023/07/29
 */
public interface UserInfoService {

	/**
	 * 根据条件查询列表
	 */
	List<UserInfo> findListByParam(UserInfoQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(UserInfoQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param);

	/**
	 * 新增
	 */
	Integer add(UserInfo bean);

	/**
	 * 批量新增
	 */

	Integer addBatch(List<UserInfo> beanList);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<UserInfo> beanList);

	/**
	 * 根据UserId查询
	 */
	UserInfo getUserInfoByUserId(String userId);

	/**
	 * 根据UserId更新
	 */
	Integer updateUserInfoByUserId(UserInfo userInfo, String userId);

	/**
	 * 根据UserId删除
	 */
	Integer deleteUserInfoByUserId(String userId);

	/**
	 * 根据Email查询
	 */
	UserInfo getUserInfoByEmail(String email);

	/**
	 * 根据Email更新
	 */
	Integer updateUserInfoByEmail(UserInfo userInfo, String email);

	/**
	 * 根据Email删除
	 */
	Integer deleteUserInfoByEmail(String email);

	/**
	 * 根据NickName查询
	 */
	UserInfo getUserInfoByNickName(String nickName);

	/**
	 * 根据NickName更新
	 */
	Integer updateUserInfoByNickName(UserInfo userInfo, String nickName);

	/**
	 * 根据NickName删除
	 */
	Integer deleteUserInfoByNickName(String nickName);

	void register(String email,String nickName,String password,String emailCode) throws BusinessException;
	void updateUserIntegral(String userId, UserIntegralOperateTypeEnum operateTypeEnum, Integer changeType, Integer integral) throws BusinessException;


	SessionWebUserDto login(String email,String password,String ip) throws BusinessException;

}