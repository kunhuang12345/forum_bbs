package com.hk.service;

import java.util.List;
import com.hk.entity.po.SysSetting;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.SysSettingQuery;

/**
 * @Description:系统设置信息Service
 * @author:AUTHOR
 * @date:2023/07/29
 */
public interface SysSettingService {

	/**
	 * 根据条件查询列表
	 */
	List<SysSetting> findListByParam(SysSettingQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(SysSettingQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<SysSetting> findListByPage(SysSettingQuery param);

	/**
	 * 新增
	 */
	Integer add(SysSetting bean);

	/**
	 * 批量新增
	 */

	Integer addBatch(List<SysSetting> beanList);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<SysSetting> beanList);

	/**
	 * 根据Code查询
	 */
	SysSetting getSysSettingByCode(String code);

	/**
	 * 根据Code更新
	 */
	Integer updateSysSettingByCode(SysSetting sysSetting, String code);

	/**
	 * 根据Code删除
	 */
	Integer deleteSysSettingByCode(String code);

}