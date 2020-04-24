package com.leyou.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.bo.SpuBo;
import com.leyou.item.service.CategoryService;
import com.leyou.item.service.GoodsService;
import com.leyou.page.PageResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandMapper brandMapper;

    /**
     * 查询商品spu信息，返回spubo，要根据cid和bid查出对应名称
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @Override
    public PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows) {
        // 设置搜索条件
        Example example = new Example(Spu.class);
        if (StringUtils.isNotBlank(key)) {
            example.createCriteria().andLike("title", "%" + key + "%");
        }
        if (saleable != null) {
            example.createCriteria().andEqualTo("saleable", saleable);
        }
        // 启动分页
        PageHelper.startPage(page, rows);
        // 查询
        List<Spu> spuList = spuMapper.selectByExample(example);
        PageInfo<Spu> spuPageInfo = new PageInfo<>(spuList);

        // 将spuList的信息传给spubo展示
        List<SpuBo> spuBoList = new ArrayList<>();
        spuList.forEach(spu -> {
            SpuBo spuBo = new SpuBo();
            // copy共同属性到新的对象
            BeanUtils.copyProperties(spu, spuBo);
            // 查询分类名称
            List<String> nameList = categoryService.queryCategoryNameByCid(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(nameList, "/"));

            // 查询品牌名称
            spuBo.setBname(brandMapper.selectByPrimaryKey(spu.getBrandId()).getName());
            spuBoList.add(spuBo);
        });
        return new PageResult<>(spuPageInfo.getPages(),spuPageInfo.getTotal(), spuBoList);
      }
}
