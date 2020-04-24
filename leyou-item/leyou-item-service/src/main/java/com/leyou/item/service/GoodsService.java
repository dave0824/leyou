package com.leyou.item.service;

import com.leyou.item.pojo.bo.SpuBo;
import com.leyou.page.PageResult;

public interface GoodsService {
    PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows);
}
