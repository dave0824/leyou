package com.leyou.page;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {
    private Integer totalPage; // 总页数
    private Long total; // 总条数
    private List<T> items; // 当前页数据

}
