package com.leyou.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.enums.ExceptionEnums;
import com.leyou.exception.LyException;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.pojo.Stock;
import com.leyou.item.pojo.bo.SpuBo;
import com.leyou.item.service.CategoryService;
import com.leyou.item.service.GoodsService;
import com.leyou.page.PageResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;

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

    /**
     * 保存商品信息
     * @param spuBo
     */
    @Transactional
    @Override
    public void saveGoods(SpuBo spuBo) {
        // 新增spu
        // 设置默认字段
        spuBo.setId(null);
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        spuMapper.insertSelective(spuBo);

        // 新增spuDetail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        spuDetailMapper.insertSelective(spuDetail);

        // 保存sku和stock
        saveSkuAndStock(spuBo);
    }

    @Override
    public SpuDetail findSpuDetailById(Long id) {
        return spuDetailMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Sku> findSkuListById(Long id) {
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> skuList = skuMapper.select(sku);
        skuList.forEach(sku1 -> {
            sku1.setStock(stockMapper.selectByPrimaryKey(sku1.getId()).getStock());
        });
        return skuList;
    }

    /**
     * 修改商品
     * @param spuBo
     */
    @Transactional
    @Override
    public void updateGoods(SpuBo spuBo) {
        // 查询以前sku
        List<Sku> skus = this.findSkuListById(spuBo.getId());
        // 如果以前存在，则删除
        if(!CollectionUtils.isEmpty(skus)) {
            List<Long> ids = skus.stream().map(s -> s.getId()).collect(Collectors.toList());
            // 删除以前库存
            Example example = new Example(Stock.class);
            example.createCriteria().andIn("skuId", ids);
            this.stockMapper.deleteByExample(example);

            // 删除以前的sku
            Sku record = new Sku();
            record.setSpuId(spuBo.getId());
            this.skuMapper.delete(record);

        }
        // 新增sku和库存
        saveSkuAndStock(spuBo);

        // 更新spu
        spuBo.setLastUpdateTime(new Date());
        spuBo.setCreateTime(null);
        spuBo.setValid(null);
        spuBo.setSaleable(null);
        this.spuMapper.updateByPrimaryKeySelective(spuBo);

        // 更新spu详情
        this.spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());
    }

    @Override
    public Spu findSpuById(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null){
            throw new LyException(ExceptionEnums.GOODS_NOT_FUND);
        }
        return spu;
    }

    /**
     * 保存sku和库存
     * @param spuBo
     */
    private void saveSkuAndStock(SpuBo spuBo) {
        spuBo.getSkus().forEach(sku -> {
            // 新增sku
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            skuMapper.insertSelective(sku);

            // 新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockMapper.insertSelective(stock);
        });

    }
}
