package com.leyou.item.pojo.bo;

import com.leyou.item.pojo.Spu;
import lombok.Data;

@Data
public class SpuBo extends Spu {

    private String cname; // 商品分类名称
    private String bname; // 品牌名称
}
