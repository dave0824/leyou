package com.leyou.item.service.impl;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamMapper specParamMapper;

    @Override
    public List<SpecGroup> queryGroupsByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);

        return specGroupMapper.select(specGroup);
    }

    @Override
    public void addSpecGroup(SpecGroup specGroup) {
        specGroupMapper.insert(specGroup);
    }

    @Override
    public void updateSpecGroup(SpecGroup specGroup) {
        specGroupMapper.updateByPrimaryKey(specGroup);
    }

    @Override
    public void deleteSpecGroup(Long id) {
        specGroupMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<SpecParam> querySpecParamsByGid(Long gid) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        return specParamMapper.select(specParam);
    }

    @Override
    public void addSpecParam(SpecParam specParam) {
        specParamMapper.insert(specParam);
    }

    @Override
    public void updateSpecParam(SpecParam specParam) {
        specParamMapper.updateByPrimaryKey(specParam);
    }

    @Override
    public void deleteSpecParam(Long id) {
        specParamMapper.deleteByPrimaryKey(id);
    }
}
