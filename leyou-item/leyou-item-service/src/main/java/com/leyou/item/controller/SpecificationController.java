package com.leyou.item.controller;


import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    /**
     * 根据类别cid查找组信息
     * @param cid
     * @return
     */
    @GetMapping("/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsByCid(@PathVariable("cid")Long cid){

        List<SpecGroup> specGroupList = specificationService.queryGroupsByCid(cid);
        return ResponseEntity.ok(specGroupList);
    }

    /**
     * 新增规格组
     * @param specGroup
     * @return
     */
    @PostMapping("/group")
    public ResponseEntity<Void> addSpecGroup(SpecGroup specGroup){
        specificationService.addSpecGroup(specGroup);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改规格组
     * @param specGroup
     * @return
     */
    @PutMapping("/group")
    public ResponseEntity<Void> updateSpecGroup(SpecGroup specGroup){
        specificationService.updateSpecGroup(specGroup);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 删除规格组
     * @param id
     * @return
     */
    @DeleteMapping("/group/{id}")
    public ResponseEntity<Void> deleteSpecGroup(@PathVariable("id")Long id){
        specificationService.deleteSpecGroup(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据组id查找组下所有参数
     * @param gid
     * @return
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> querySpecParamsByGid(@RequestParam("gid")Long gid){

        return ResponseEntity.ok( specificationService.querySpecParamsByGid(gid));
    }

    /**
     * 新增参数
     * @param specParam
     * @return
     */
    @PostMapping("/param")
    public ResponseEntity<Void> addSpecParam(SpecParam specParam){
        specificationService.addSpecParam(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改参数
     * @param specParam
     * @return
     */
    @PutMapping("/param")
    public ResponseEntity<Void> updateSpecParam(SpecParam specParam){
        specificationService.updateSpecParam(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 删除参数
     * @param id
     * @return
     */
    @DeleteMapping("/param/{id}")
    public ResponseEntity<Void> deleteSpecParam(@PathVariable("id")Long id){
        specificationService.deleteSpecParam(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
