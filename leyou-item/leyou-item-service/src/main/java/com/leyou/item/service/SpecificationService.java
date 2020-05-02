package com.leyou.item.service;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;

import java.util.List;

public interface SpecificationService {
    List<SpecGroup> queryGroupsByCid(Long cid);

    void addSpecGroup(SpecGroup specGroup);

    void updateSpecGroup(SpecGroup specGroup);

    void deleteSpecGroup(Long id);

    List<SpecParam> querySpecParamsByGid(Long gid);

    void addSpecParam(SpecParam specParam);

    void updateSpecParam(SpecParam specParam);

    void deleteSpecParam(Long id);

    List<SpecParam> querySpecParamsByCid(Long cid);

    List<SpecParam> queryParams(Long gid, Long cid, Boolean generic, Boolean searching);
}
