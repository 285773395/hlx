package com.zslin.wx.controller;

import com.zslin.web.service.ICategoryService;
import com.zslin.web.service.IGalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 钟述林 393156105@qq.com on 2017/2/24 15:07.
 */
@Controller
@RequestMapping(value = "wx")
public class WeixinIndexController {

    @Autowired
    private IGalleryService galleryService;

    @Autowired
    private ICategoryService categoryService;

    @GetMapping(value = "index")
    public String index(Model model, HttpServletRequest request) {

        model.addAttribute("categoryList", categoryService.findByOrder()); //分类
        model.addAttribute("galleryList", galleryService.findShow()); //微信画廊
        return "weixin/index/index";
    }
}
