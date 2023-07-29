package com.hk.service.impl;

import java.util.List;
import java.util.Date;
import java.util.Objects;

import com.hk.entity.config.WebConfig;
import com.hk.entity.constants.Constants;
import com.hk.entity.po.UserInfo;
import com.hk.entity.query.UserInfoQuery;
import com.hk.exception.BusinessException;
import com.hk.mapper.UserInfoMapper;
import com.hk.utils.DateUtils;
import com.hk.entity.enums.DateTimePatternEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hk.utils.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import com.hk.service.EmailCodeService;
import com.hk.entity.po.EmailCode;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.EmailCodeQuery;
import com.hk.mapper.EmailCodeMapper;
import com.hk.entity.query.SimplePage;
import com.hk.entity.enums.PageSize;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @Description:邮箱验证码ServiceImpl
 * @author:AUTHOR
 * @date:2023/07/29
 */
@Service("emailCodeService")
public class EmailCodeServiceImpl implements EmailCodeService {

	private static final Logger logger = LoggerFactory.getLogger(EmailCodeServiceImpl.class);
	@Resource
	UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

	@Resource
	private EmailCodeMapper<EmailCode, EmailCodeQuery> emailCodeMapper;

	@Resource
	private JavaMailSender javaMailSender;

	@Resource
	private WebConfig webConfig;

	/**
	 * 根据条件查询列表
	 */
	public List<EmailCode> findListByParam(EmailCodeQuery query) {
		return this.emailCodeMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(EmailCodeQuery query) {
		return this.emailCodeMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<EmailCode> findListByPage(EmailCodeQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<EmailCode> list = this.findListByParam(query);
		PaginationResultVO<EmailCode> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(EmailCode bean) {
		return this.emailCodeMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<EmailCode> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.emailCodeMapper.insertBatch(beanList);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<EmailCode> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.emailCodeMapper.insertOrUpdateBatch(beanList);
	}

	/**
	 * 根据EmailAndCode查询
	 */
	public EmailCode getEmailCodeByEmailAndCode(String email, String code) {
		return this.emailCodeMapper.selectByEmailAndCode(email, code);
	}

	/**
	 * 根据EmailAndCode更新
	 */
	public Integer updateEmailCodeByEmailAndCode(EmailCode emailCode, String email, String code) {
		return this.emailCodeMapper.updateByEmailAndCode(emailCode, email, code);
	}

	/**
	 * 根据EmailAndCode删除
	 */
	public Integer deleteEmailCodeByEmailAndCode(String email, String code) {
		return this.emailCodeMapper.deleteByEmailAndCode(email, code);
	}

	/**
	 * 发送邮件验证码
	 * @param email
	 * @param type
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void sendEmailCode(String email, Integer type) throws BusinessException {
		if (Objects.equals(type, Constants.ZERO)){
			UserInfo userInfo = userInfoMapper.selectByEmail(email);
			if (userInfo != null){
				throw new BusinessException("邮箱已存在");
			}
		}
		String code = StringTools.getRandomString(Constants.LENGTH_5);
		sendEmailCodeDo(email,code);
		emailCodeMapper.disableEmailCode(email);

		EmailCode emailCode = new EmailCode();
		emailCode.setCode(code);
		emailCode.setCreateTime(new Date());
		emailCode.setStatus(Constants.ZERO);
		emailCode.setEmail(email);
		emailCodeMapper.insert(emailCode);
	}

	private void sendEmailCodeDo(String toEmail,String code) throws BusinessException {
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message,true);
			// 邮件发送人
			helper.setFrom(webConfig.getSendUserName());
			// 邮件收件人
			helper.setTo(toEmail);

			helper.setSubject("注册邮箱验证码");
			helper.setText("邮箱验证码为" + code);
			helper.setSentDate(new Date());
			javaMailSender.send(message);
		} catch (Exception e) {
			logger.error("邮件发送失败",e);
			throw new BusinessException("邮件发送失败");
		}
		// 邮件发送人
	}

}