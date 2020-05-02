package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/brand")
public interface BrandApi {
    @GetMapping("/id")
     Brand findById(@RequestParam("id")Long id);
}
