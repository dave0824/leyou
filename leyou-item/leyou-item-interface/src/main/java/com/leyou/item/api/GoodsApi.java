package com.leyou.item.api;

import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.pojo.bo.SpuBo;
import com.leyou.page.PageResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {
    @GetMapping("/spu/page")
    PageResult<SpuBo> querySpuBoByPage(
            @RequestParam(value = "key", required = false)String key,
            @RequestParam(value = "saleable", required = false)Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1")Integer page,
            @RequestParam(value = "rows", defaultValue = "5")Integer rows
    );
    @GetMapping("/spu/detail/{id}")
    SpuDetail findSpuDetailById(@PathVariable("id") Long id);

    @GetMapping("/sku/list")
    List<Sku> findSkuListById(@RequestParam("id") Long id);
}
