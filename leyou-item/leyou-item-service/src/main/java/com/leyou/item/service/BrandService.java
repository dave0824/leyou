package com.leyou.item.service;

import com.leyou.item.pojo.Brand;
import com.leyou.page.PageResult;

import java.util.List;

public interface BrandService {
    PageResult<Brand> queryBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc);

    void saveBrand(Brand brand, List<Long> cids);

    void updateBrand(Brand brand, List<Long> cids);

    void deleteBrand(Long id);

    List<Brand> findByCid(Long cid);

    Brand findById(Long id);
}
