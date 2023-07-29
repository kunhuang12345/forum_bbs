package com.hk.service.impl;

import java.util.List;
import com.hk.service.SysSettingService;
import com.hk.entity.po.SysSetting;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.SysSettingQuery;
import com.hk.mapper.SysSettingMapper;
import com.hk.entity.query.SimplePage;
import com.hk.entity.enums.PageSize;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description:系统设置信息ServiceImpl
 * @author:AUTHOR
 * @date:2023/07/29
 */
@Service("sysSettingService")
public class SysSettingServiceImpl implements SysSettingService {

	@Resource
	private SysSettingMapper<SysSetting, SysSettingQuery> sysSettingMapper;

	/**
	 * 根据条件查询列表
	 */
	public List<SysSetting> findListByParam(SysSettingQuery query) {
		return this.sysSettingMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(SysSettingQuery query) {
		return this.sysSettingMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<SysSetting> findListByPage(SysSettingQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<SysSetting> list = this.findListByParam(query);
		PaginationResultVO<SysSetting> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(SysSetting bean) {
		return this.sysSettingMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<SysSetting> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.sysSettingMapper.insertBatch(beanList);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<SysSetting> beanList) {
		if (beanList == null || beanList.isEmpty()) {
			return 0;
		}
		return this.sysSettingMapper.insertOrUpdateBatch(beanList);
	}

	/**
	 * 根据Code查询
	 */
	public SysSetting getSysSettingByCode(String code) {
		return this.sysSettingMapper.selectByCode(code);
	}

	/**
	 * 根据Code更新
	 */
	public Integer updateSysSettingByCode(SysSetting sysSetting, String code) {
		return this.sysSettingMapper.updateByCode(sysSetting, code);
	}

	/**
	 * 根据Code删除
	 */
	public Integer deleteSysSettingByCode(String code) {
		return this.sysSettingMapper.deleteByCode(code);
	}

}