package com.hk.service;

import java.util.List;
import java.util.Date;

import com.hk.exception.BusinessException;
import com.hk.utils.DateUtils;
import com.hk.entity.enums.DateTimePatternEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.hk.entity.po.EmailCode;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.EmailCodeQuery;

/**
 * @Description:邮箱验证码Service
 * @author:AUTHOR
 * @date:2023/07/29
 */
public interface EmailCodeService {

	/**
	 * 根据条件查询列表
	 */
	List<EmailCode> findListByParam(EmailCodeQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(EmailCodeQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<EmailCode> findListByPage(EmailCodeQuery param);

	/**
	 * 新增
	 */
	Integer add(EmailCode bean);

	/**
	 * 批量新增
	 */

	Integer addBatch(List<EmailCode> beanList);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<EmailCode> beanList);

	/**
	 * 根据EmailAndCode查询
	 */
	EmailCode getEmailCodeByEmailAndCode(String email, String code);

	/**
	 * 根据EmailAndCode更新
	 */
	Integer updateEmailCodeByEmailAndCode(EmailCode emailCode, String email, String code);

	/**
	 * 根据EmailAndCode删除
	 */
	Integer deleteEmailCodeByEmailAndCode(String email, String code);

	/**
	 * 发邮件验证码
	 * @param email
	 * @param type
	 */
	void sendEmailCode(String email,Integer type) throws BusinessException;

}