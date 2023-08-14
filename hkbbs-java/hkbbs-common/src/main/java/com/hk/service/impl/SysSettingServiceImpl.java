package com.hk.service.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.List;

import com.hk.entity.dto.SysSetting4AuditDto;
import com.hk.entity.dto.SysSetting4CommentDto;
import com.hk.entity.dto.SysSettingDto;
import com.hk.entity.enums.SysSettingCodeEnum;
import com.hk.exception.BusinessException;
import com.hk.service.SysSettingService;
import com.hk.entity.po.SysSetting;
import com.hk.entity.vo.PaginationResultVO;
import com.hk.entity.query.SysSettingQuery;
import com.hk.mapper.SysSettingMapper;
import com.hk.entity.query.SimplePage;
import com.hk.entity.enums.PageSize;
import com.hk.utils.JsonUtils;
import com.hk.utils.StringTools;
import com.hk.utils.SysCacheUtils;
import com.mysql.cj.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @Description:系统设置信息ServiceImpl
 * @author:AUTHOR
 * @date:2023/07/29
 */
@Service("sysSettingService")
public class SysSettingServiceImpl implements SysSettingService {

    private static final Logger logger = LoggerFactory.getLogger(SysSettingServiceImpl.class);

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

    @Override
    public SysSettingDto refreshCache() throws BusinessException {
        try {
            SysSettingDto sysSettingDto = new SysSettingDto();
            List<SysSetting> list = this.sysSettingMapper.selectList(new SysSettingQuery());
            for (SysSetting sysSetting : list
            ) {
                String jsonContent = sysSetting.getJsonContent();
                if (StringTools.isEmpty(jsonContent)) {
                    continue;
                }
                String code = sysSetting.getCode();
                SysSettingCodeEnum sysSettingCodeEnum = SysSettingCodeEnum.getByCode(code);
                PropertyDescriptor pd = new PropertyDescriptor(sysSettingCodeEnum.getPropName(), SysSettingDto.class);
                Method method = pd.getWriteMethod();
                Class clazz = Class.forName(sysSettingCodeEnum.getClazz());
                method.invoke(sysSettingDto, JsonUtils.convertJson2Obj(jsonContent, clazz));
            }
            SysCacheUtils.refresh(sysSettingDto);
            return sysSettingDto;
        } catch (Exception e) {
            logger.error("刷新缓存失败", e);
        }
        throw new BusinessException("刷新缓存失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSetting(SysSettingDto sysSettingDto) throws BusinessException {
        try {
            Class clazz = SysSettingService.class;
            for (SysSettingCodeEnum codeEnum : SysSettingCodeEnum.values()) {
                // 取类中的codeEnum.getPropName属性
                PropertyDescriptor pd = new PropertyDescriptor(codeEnum.getPropName(), clazz);

                // 取属性的getter方法
                Method method = pd.getReadMethod();
                // 调用sysSettingDto的getter方法
                Object invoke = method.invoke(sysSettingDto);
                SysSetting sysSetting = new SysSetting();
                sysSetting.setCode(codeEnum.getCode());
                sysSetting.setJsonContent(JsonUtils.convertObj2Json(invoke));
                sysSettingMapper.insertOrUpdate(sysSetting);
            }

        } catch (Exception e) {
            logger.error("保存设置失败",e);
            throw new BusinessException("保存设置失败");
        }
    }
}