package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 查询分类列表
     * @param pid
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<Category>> queryCategoryByPid(@PathParam("pid")Long pid){
        return ResponseEntity.ok(categoryService.queryCategoryByPid(pid));
    }

    /**
     * 添加类别
     * @param category
     * @return
     */
    @PostMapping("/add")
    public ResponseEntity<Long> addCategory(Category category){
        Long id = categoryService.addCategory(category);
        return ResponseEntity.ok(id);
    }

    /**
     * 修改类别
     * @param id
     * @param name
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCategory(@PathVariable("id")Long id,@RequestParam("name") String name){
        System.out.println(id + name);
        categoryService.updateCategory(id,name);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改父类别为是父类别
     * @param id
     * @return
     */
    @PutMapping("/isParent/{id}")
    public ResponseEntity<Void> updateParentIsParent(@PathVariable("id")Long id){
        categoryService.updateParentIsParent(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 删除类别
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据商品id查找商品所属类名称
     * @param bid
     * @return
     */
 /*   @GetMapping("/bid/{bid}")
    public ResponseEntity<List<String>> queryCategoryNameByBid(@PathVariable("bid")Long bid){
        return ResponseEntity.ok(categoryService.queryCategoryNameByBid(bid));
    }*/

    /**
     * 根据商品id查找商品所属类别
     * @param bid
     * @return
     */
    @GetMapping("/bid/{bid}")
    public ResponseEntity<List<Category>> queryCategoryNameByBid(@PathVariable("bid")Long bid){
        return ResponseEntity.ok(categoryService.queryCategoriesByBid(bid));
    }

    /**
     * 根据分类id查找分类名
     * @param ids
     * @return
     */
    @GetMapping("/names")
    public ResponseEntity<List<String>> queryCategoryNameByCids(@RequestParam("ids") List<Long> ids){
        return ResponseEntity.ok(categoryService.queryCategoryNameByCids(ids));
    }
}
