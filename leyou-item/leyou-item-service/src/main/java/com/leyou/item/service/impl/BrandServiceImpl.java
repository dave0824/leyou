package com.leyou.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import com.leyou.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;
    /**
     * 根据条件分页查询并排序品牌信息
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    @Override
    public PageResult<Brand> queryBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {

        // 初始化example对象
        Example example = new Example(Brand.class);
        // 根据name模糊查询，或者根据首字母查询
        if (StringUtil.isNotEmpty(key)){
            example.createCriteria().andLike("name","%" + key + "%").orEqualTo("letter",key);
        }
        // 添加分页条件
        PageHelper.startPage(page,rows);
        // 添加排序条件
        if (StringUtil.isNotEmpty(sortBy)){
            example.setOrderByClause(sortBy + " " + (desc ? "desc" : "asc"));
        }
        List<Brand> brands = brandMapper.selectByExample(example);
        // 包装成pageInfo
        PageInfo<Brand> brandPageInfo = new PageInfo<>(brands);
        // 包装成分页结果集返回
        return new PageResult<Brand>(brandPageInfo.getPages(),brandPageInfo.getTotal(),brandPageInfo.getList());
    }

    @Override
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        // 1.新增brand
        brand.setId(null); // 因为id是自增长的，所有设置为空
        brandMapper.insert(brand);
        // 2.在中间表tb_category_brand插入数据
       cids.forEach(cid -> {
           brandMapper.insertCategoryAndBrand(cid,brand.getId());
       });
    }

    @Override
    @Transactional
    public void updateBrand(Brand brand, List<Long> cids) {
        // 1.更新brand
        brandMapper.updateByPrimaryKey(brand);
        // 2.删除商品原属类型
        brandMapper.deleteCategoryAndBrand(brand.getId());
        // 3.在中间表tb_category_brand插入数据
        cids.forEach(cid -> {
            brandMapper.insertCategoryAndBrand(cid,brand.getId());
        });
    }

    /**
     * 删除商品并删除商品类型
     * @param bid
     */
    @Override
    @Transactional
    public void deleteBrand(Long bid) {
        brandMapper.deleteCategoryAndBrand(bid);
        brandMapper.deleteByPrimaryKey(bid);
    }

    /**
     * 根据cid查找brand列表
     * @param cid
     * @return
     */
    @Override
    public List<Brand> findByCid(Long cid) {
        List<Long> bidList = brandMapper.findBidByCid(cid);
        List<Brand> brands = brandMapper.selectByIdList(bidList);
        return brands;
    }
}
