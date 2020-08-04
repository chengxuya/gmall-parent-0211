package com.att.gmall.all.controller;

import com.att.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CartController {
//    @Autowired
//    CartFeignClient cartFeignClient;
    @RequestMapping("addCart.html")
    public String  addCart(Long skuId, Long skuNum){

        //调用购物车的添加业务
//            CartInfo cartInfo=cartFeignClient.addCart(skuId,skuNum);
        return  "redirect:http://cart.gmall.com/cartSuccess?skuName=1";
    }

    @RequestMapping("cartSuccess")
    public String  cartSuccess(CartInfo cartInfo){

        return  "cart/addCart";
    }
}
