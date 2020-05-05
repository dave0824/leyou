package com.leyou.service;

import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecificationClient;
import com.leyou.item.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class GoodsService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    public Map<String, Object> toItemPage(Long spuId) {

        HashMap<String, Object> map = new HashMap<>();
        // 查询spu
        Spu spu = goodsClient.findSpuById(spuId);

        // 查询参数组
        List<SpecGroup> groups = specificationClient.queryParamGroup(spu.getCid3());
        // 查询参数
        List<SpecParam> params = specificationClient.queryParams(null, spu.getCid3(), null, false);
        // 将参数封装成map,用于将param放置到对应的组里面
        Map<Long,List> paramsMap = new HashMap<>();
        for (SpecParam param : params) {
            if (CollectionUtils.isEmpty(paramsMap.get(param.getGroupId()))){
                ArrayList<Object> paramList = new ArrayList<>();
                paramsMap.put(param.getGroupId(),paramList);
            }
            paramsMap.get(param.getGroupId()).add(param);
        }

        for (SpecGroup group : groups) {
            group.setParams(paramsMap.get(group.getId()));
        }

        // 将参数封装成map,用于将显示
        Map<Long, String> paramMap = new HashMap<>();

        params.forEach(param -> {
            paramMap.put(param.getId(), param.getName());
        });

        // 查询分类
        List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = categoryClient.queryCategoryNameByCids(cids);
        List<Map<String,Object>> categories = new ArrayList<>();
        for (int i = 0; i < cids.size(); i++) {
            Map<String, Object> categoryMap = new HashMap<>();
            categoryMap.put("id", cids.get(i));
            categoryMap.put("name", names.get(i));
            categories.add(categoryMap);
        }

        // 查询spu详情
        SpuDetail spuDetail = goodsClient.findSpuDetailById(spuId);
        // 查询spu下的sku
        List<Sku> skus = goodsClient.findSkuListById(spuId);
        // 查询商品
        Brand brand = brandClient.findById(spu.getBrandId());

        map.put("groups",groups);
        map.put("paramMap",paramMap);
        map.put("categories",categories);
        map.put("spu",spu);
        map.put("spuDetail",spuDetail);
        map.put("skus",skus);
        map.put("brand",brand);
        return map;
    }
}
