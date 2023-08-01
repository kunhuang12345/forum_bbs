package com.hk.service.impl;

import java.util.List;
import java.util.Date;

import com.hk.entity.constants.Constants;
import com.hk.entity.enums.*;
import com.hk.entity.po.UserIntegralRecord;
import com.hk.entity.po.UserMessage;
import com.hk.entity.query.UserIntegralRecordQuery;
import com.hk.entity.query.UserMessageQuery;
import com.hk.exception.BusinessException;
import com.hk.mapper.UserIntegralRecordMapper;
import com.hk.mapper.UserMessageMapper;
import com.hk.service.EmailCodeService;
import com.hk.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hk.utils.StringTools;
import com.hk.utils.SysCacheUtils;
import org.springframework.format.annotation.DateTimeFormat;
import com.hk.service.UserInfoService;
import com.hk.entity.po.UserInfo;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.UserInfoQuery;
import com.hk.mapper.UserInfoMapper;
import com.hk.entity.query.SimplePage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @Description:用户信息ServiceImpl
 * @author:AUTHOR
 * @date:2023/07/29
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

	@Resource
	private EmailCodeService emailCodeService;

	@Resource
	private UserMessageMapper<UserMessage, UserMessageQuery> userMessageMapper;

	@Resource
	private UserIntegralRecordMapper<UserIntegralRecord, UserIntegralRecordQuery> userIntegralRecordMapper;

	/**
	 * 根据条件查询列表
	 */
	public List<UserInfo> findListByParam(UserInfoQuery query) {
		return this.userInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(UserInfoQuery query) {
		return this.userInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<UserInfo> list = this.findListByParam(query);
		PaginationResultVO<UserInfo> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(UserInfo bean) {
		return this.userInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<UserInfo> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertBatch(beanList);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<UserInfo> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertOrUpdateBatch(beanList);
	}

	/**
	 * 根据UserId查询
	 */
	public UserInfo getUserInfoByUserId(String userId) {
		return this.userInfoMapper.selectByUserId(userId);
	}

	/**
	 * 根据UserId更新
	 */
	public Integer updateUserInfoByUserId(UserInfo userInfo, String userId) {
		return this.userInfoMapper.updateByUserId(userInfo, userId);
	}

	/**
	 * 根据UserId删除
	 */
	public Integer deleteUserInfoByUserId(String userId) {
		return this.userInfoMapper.deleteByUserId(userId);
	}

	/**
	 * 根据Email查询
	 */
	public UserInfo getUserInfoByEmail(String email) {
		return this.userInfoMapper.selectByEmail(email);
	}

	/**
	 * 根据Email更新
	 */
	public Integer updateUserInfoByEmail(UserInfo userInfo, String email) {
		return this.userInfoMapper.updateByEmail(userInfo, email);
	}

	/**
	 * 根据Email删除
	 */
	public Integer deleteUserInfoByEmail(String email) {
		return this.userInfoMapper.deleteByEmail(email);
	}

	/**
	 * 根据NickName查询
	 */
	public UserInfo getUserInfoByNickName(String nickName) {
		return this.userInfoMapper.selectByNickName(nickName);
	}

	/**
	 * 根据NickName更新
	 */
	public Integer updateUserInfoByNickName(UserInfo userInfo, String nickName) {
		return this.userInfoMapper.updateByNickName(userInfo, nickName);
	}

	/**
	 * 根据NickName删除
	 */
	public Integer deleteUserInfoByNickName(String nickName) {
		return this.userInfoMapper.deleteByNickName(nickName);
	}

	/**
	 * 注册业务
	 * @param email
	 * @param nickName
	 * @param password
	 * @param emailCode
	 * @throws BusinessException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void register(String email, String nickName, String password,String emailCode) throws BusinessException {
		UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
		if (null != userInfo){
			throw new BusinessException("邮箱账号已存在");
		}
		userInfo = this.userInfoMapper.selectByNickName(nickName);
		if (userInfo != null) {
			throw new BusinessException("用户名已存在");
		}
		emailCodeService.checkCode(email,emailCode);
		String userId = StringTools.getRandomNumber(Constants.LENGTH_10);
		while(userInfoMapper.selectByUserId(userId) != null){
			userId = StringTools.getRandomNumber(Constants.LENGTH_10);
		}
		UserInfo userInfoNew = new UserInfo();
		userInfoNew.setEmail(email);
		userInfoNew.setUserId(userId);
		userInfoNew.setNickName(nickName);
		userInfoNew.setPassword(StringTools.encodeMd5(password));
		userInfoNew.setJoinTime(new Date());
		userInfoNew.setStatus(UserStatus.ENABLE.getCode());
		userInfoNew.setTotalIntegral(0);
		userInfoNew.setCurrentIntegral(0);
		this.userInfoMapper.insert(userInfoNew);

		// 更新用户积分
		this.updateUserIntegral(userId,UserIntegralOperateTypeEnum.REGISTER,UserIntegralChangeTypeEnum.ADD.getChangeType(), Constants.INTEGRAL_5);

		// 记录消息
		UserMessage userMessage = new UserMessage();
		userMessage.setReceivedUserId(userId);
		userMessage.setMessageType(MessageTypeEnum.SYS.getType());
		userMessage.setCreateTime(new Date());
		userMessage.setStatus(MessageStatusEnum.NO_READ.getStatus());
		userMessage.setMessageContent(SysCacheUtils.getSysSetting().getRegisterSetting().getRegisterWelcomeInfo());
		userMessageMapper.insert(userMessage);
	}

	/**
	 * 更新用户积分
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateUserIntegral(String userId,UserIntegralOperateTypeEnum operateTypeEnum,Integer changeType,Integer integral) throws BusinessException {
		integral = changeType*integral;
		if (integral == 0) {
			return;
		}
		UserInfo userInfo = userInfoMapper.selectByUserId(userId);
		if (UserIntegralChangeTypeEnum.REDUCE.getChangeType().equals(changeType) && userInfo.getCurrentIntegral() + integral < 0) {
			integral = changeType*userInfo.getCurrentIntegral();
		}
		UserIntegralRecord userIntegralRecord = new UserIntegralRecord();
		userIntegralRecord.setUserId(userId);
		userIntegralRecord.setOperType(operateTypeEnum.getOperateType());
		userIntegralRecord.setCreateTime(new Date());
		userIntegralRecord.setIntegral(integral);
		userIntegralRecordMapper.insert(userIntegralRecord);

		Integer count = this.userInfoMapper.updateIntegral(userId,integral);
		if (count == 0){
			throw new BusinessException("更新用户积分失败");
		}
	}
}