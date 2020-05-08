package com.leyou.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum  ExceptionEnums {
    IMAGE_TYPE_NOT_CONFORM(400,"图片类型不符合"),
    ADD_CATEGORY_FAIL(500,"添加类别错误"),
    UPDATE_CATEGORY_FAIL(500,"跟新类别失败"),
    DELETE_CATEGORY_FAIL(500,"删除类别失败"),
    CATEGORY_NOT_FOUND(404,"商品类型没找到"),
    GOODS_NOT_FUND(404,"商品没找到"),
    USER_DATA_ARGS_ERROR(400,"参数有误"),
    ;
    private int code;
    private String msg;
}
