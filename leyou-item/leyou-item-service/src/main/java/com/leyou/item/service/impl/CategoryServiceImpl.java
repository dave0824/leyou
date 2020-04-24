package com.leyou.item.service.impl;

import com.leyou.enums.ExceptionEnums;
import com.leyou.exception.LyException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public List<Category> queryCategoryByPid(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        List<Category> categoryList = categoryMapper.select(category);
        if (CollectionUtils.isEmpty(categoryList)){
            throw new LyException(ExceptionEnums.CATEGORY_NOT_FOUND);
        }
        return categoryList;
    }

    @Override
    public Long addCategory(Category category) {
        category.setId(null);
        int insert = categoryMapper.insert(category);
        if (insert == -1){
            throw new LyException(ExceptionEnums.ADD_CATEGORY_FAIL);
        }
        return category.getId();
    }

    /**
     * 修改类别，先查再改
     * @param id
     * @param name
     */
    @Override
    public void updateCategory(Long id, String name) {
        Category category = categoryMapper.selectByPrimaryKey(id);
        category.setName(name);
        int key = categoryMapper.updateByPrimaryKey(category);
        if (key == -1){
            throw new LyException(ExceptionEnums.UPDATE_CATEGORY_FAIL);
        }
    }

    @Override
    public void deleteCategory(Long id) {
        int delete = categoryMapper.deleteByPrimaryKey(id);
        if (delete == -1){
            throw new LyException(ExceptionEnums.DELETE_CATEGORY_FAIL);
        }
    }

    /**
     * 修改父类别为是父类别,先查后改
     * @param id
     */
    @Override
    public void updateParentIsParent(Long id) {
        Category category = categoryMapper.selectByPrimaryKey(id);
        category.setIsParent(true);
        categoryMapper.updateByPrimaryKey(category);
    }

    /**
     * 根据bid查找出cid，然后根据cid查找出具体类别名
     * @param bid
     * @return
     */
    @Override
    public List<String> queryCategoryNameByBid(Long bid) {
        List<Long> cids = categoryMapper.queryCategoryNameByBid(bid);
        List<Category> categories = categoryMapper.selectByIdList(cids);
        List<String> stringList = categories.stream().map(Category::getName).collect(Collectors.toList());
        return stringList;
    }

    @Override
    public List<Category> queryCategoriesByBid(Long bid) {
        List<Long> cids = categoryMapper.queryCategoryNameByBid(bid);
        List<Category> categories = categoryMapper.selectByIdList(cids);
        return categories;
    }

    /**
     * 根据cids查找cname
     * @param cidList
     * @return
     */
    @Override
    public List<String> queryCategoryNameByCid(List<Long> cidList) {
        List<String> stringList = new ArrayList<>();
        cidList.forEach(cid -> {
            Category category = categoryMapper.selectByPrimaryKey(cid);
            stringList.add(category.getName());
        });
        return stringList;
    }
}
