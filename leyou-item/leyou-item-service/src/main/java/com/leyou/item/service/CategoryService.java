package com.leyou.item.service;

import com.leyou.item.pojo.Category;

import java.util.List;

public interface CategoryService {
    List<Category> queryCategoryByPid(Long pid);

    Long addCategory(Category category);

    void updateCategory(Long id, String name);

    void deleteCategory(Long id);

    void updateParentIsParent(Long id);

    List<String> queryCategoryNameByBid(Long bid);

    List<Category> queryCategoriesByBid(Long bid);

    List<String> queryCategoryNameByCid(List<Long> cidList);
}
