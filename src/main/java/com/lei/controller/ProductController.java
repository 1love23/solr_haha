package com.lei.controller;

import com.lei.pojo.Result;
import com.lei.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value="/search")
public class ProductController {
    @Autowired
    private ProductService productService;

    @RequestMapping(value="/list")
    public String list(@RequestParam(value = "queryString",required = false, defaultValue = "")String queryString,
                       @RequestParam(value = "catalog_name",required = false)String catalog_name,
                       @RequestParam(value = "price",required = false)String price,
                       @RequestParam(value = "page",required = false,defaultValue = "1")int page,
                       @RequestParam(value = "sort",required = false,defaultValue = "0")int sort, Model model) throws Exception {
        Result result = productService.queryProduct(queryString,catalog_name,price,page,sort);
        model.addAttribute("result",result);
        model.addAttribute("queryString",queryString);
        model.addAttribute("catalog_name",catalog_name);
        model.addAttribute("price",price);
        model.addAttribute("page",page);
        model.addAttribute("sort",sort);

      return "product_list";
    }
}
