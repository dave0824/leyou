package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.item.pojo.*;
import com.leyou.page.PageResult;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class SearchService {
    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private GoodsRepository goodsRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Goods buildGoods(Spu spu) throws IOException {
        // 新建商品
        Goods goods = new Goods();
        // 查找品牌
        Brand brand = brandClient.findById(spu.getBrandId());
        // 查询分类名称
        List<String> names = categoryClient.queryCategoryNameByCids(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        // 查询spu下的所有sku
        List<Sku> skus = goodsClient.findSkuListById(spu.getId());
        List<Long> prices = new ArrayList<>();
        List<Map<String, Object>> skuMapList = new ArrayList<>();
        // 遍历skus，获取价格,sku集合
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id",sku.getId());
            skuMap.put("title",sku.getTitle());
            skuMap.put("price",sku.getPrice());
            skuMap.put("image",StringUtils.isNotBlank(sku.getImages()) ?
                    StringUtils.split(sku.getImages(),",")[0] : "");
            skuMapList.add(skuMap);

        });
        // 查询出所有的搜索规格参数
        List<SpecParam> params = specificationClient.queryParams(null, spu.getCid3(), null, true);
        // 查询spuDetail。获取规格参数值
        SpuDetail spuDetail = goodsClient.findSpuDetailById(spu.getId());
        // 获取通用的规格参数
        Map<Long, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<Long, Object>>() {
        });
        // 获取特殊的规格参数
        Map<Long, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<Object>>>() {
        });
        // 定义map接收{规格参数名，规格参数值}
        Map<String, Object> paramMap = new HashMap<>();
        params.forEach(param -> {
            // 判断是否通用规格参数
            if (param.getGeneric()) {
                // 获取通用规格参数值
                String value = genericSpecMap.get(param.getId()).toString();
                // 判断是否是数值类型
                if (param.getNumeric()){
                    // 如果是数值的话，判断该数值落在那个区间
                    value = chooseSegment(value, param);
                }
                // 把参数名和值放入结果集中
                paramMap.put(param.getName(), value);
            } else {
                paramMap.put(param.getName(), specialSpecMap.get(param.getId()));
            }
        });




        // 设置参数
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        goods.setAll(spu.getTitle() + brand.getName() + StringUtils.join(names, " "));
        goods.setPrice(prices);
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        goods.setSpecs(paramMap);

        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /**
     * 分页搜索商品信息
     * @param searchRequest
     * @return
     */
    public PageResult<Goods> search(SearchRequest searchRequest) {

        // 1. 判断key为null.不让查所有
        if (! StringUtils.isNotBlank(searchRequest.getKey())){
            return null;
        }

        // 建立查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加全文检索
        queryBuilder.withQuery(QueryBuilders.matchQuery("all",
                searchRequest.getKey()).operator(Operator.AND));
        // 判断是否排序
        if (StringUtils.isNotBlank(searchRequest.getSortBy())){
            queryBuilder.withSort(SortBuilders.fieldSort(searchRequest.getSortBy())
                    .order(searchRequest.getDescending() ? SortOrder.DESC : SortOrder.ASC));
        }

        // 通过sourceFilter设置返回的结果字段,我们只需要id、skus、subTitle
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));

        // 分页
        int page = searchRequest.getPage();
        int size = searchRequest.getSize();
        queryBuilder.withPageable(PageRequest.of(page-1,size));

        // 查询结果
        Page<Goods> goodsPage = goodsRepository.search(queryBuilder.build());

        // 封装返回结果
        return new PageResult<>(goodsPage.getTotalPages(),goodsPage.getTotalElements(),goodsPage.getContent());

    }

    /**
     * 创建或修改一条记录到索引库
     * @param id
     * @throws IOException
     */
    public void createIndex(Long id) throws IOException {

        Spu spu = this.goodsClient.findSpuById(id);
        // 构建商品
        Goods goods = this.buildGoods(spu);

        // 保存数据到索引库
        goodsRepository.save(goods);
    }

    /**
     * 删除对应的spu,根据id
     * @param id
     */
    public void deleteIndex(Long id) {
        goodsRepository.deleteById(id);
    }
}
