package com.leyou.controller;

import com.leyou.service.GoodsHtmlService;
import com.leyou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsHtmlService goodsHtmlService;

    @RequestMapping("/item/{id}.html")
    public String toItemPage(@PathVariable("id")Long spuId, Model model){

        model.addAllAttributes(goodsService.toItemPage(spuId));
        goodsHtmlService.asyncExcute(spuId);
        return "item";
    }
}
