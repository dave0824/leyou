package com.leyou.item.service;

import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.pojo.bo.SpuBo;
import com.leyou.page.PageResult;

import java.util.List;

public interface GoodsService {
    PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows);

    void saveGoods(SpuBo spuBo);

    SpuDetail findSpuDetailById(Long id);

    List<Sku> findSkuListById(Long id);

    void updateGoods(SpuBo spuBo);

    Spu findSpuById(Long id);
}
