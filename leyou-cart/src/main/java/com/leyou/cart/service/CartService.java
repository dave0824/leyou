package com.leyou.cart.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.cart.interceptor.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    static final String KEY_PREFIX = "leyou:cart:uid:";

    /**
     * 添加购物车
     * @param cart
     */
    public void addCart(Cart cart) {
        // 获取用户
        UserInfo user = LoginInterceptor.getLoginUser();
        // 得到购物车
        BoundHashOperations<String, Object, Object> hashOps = stringRedisTemplate.boundHashOps(KEY_PREFIX + user.getId());
        // 查询是sku否存在
        Long skuId = cart.getSkuId();
        Integer num = cart.getNum();
        Boolean aBoolean = hashOps.hasKey(skuId.toString());
        if (aBoolean){
            // 获取购物车中的数据
            String json = hashOps.get(skuId.toString()).toString();
            cart = JsonUtils.parse(json,Cart.class);
            cart.setNum(cart.getNum() + num);
        }else {
            cart.setUserId(user.getId());
        }
        // 存回Redis
        hashOps.put(skuId.toString(), JsonUtils.serialize(cart));

    }

    public List<Cart> queryCartList() {
        // 获取用户
        UserInfo user = LoginInterceptor.getLoginUser();
        // 判断是否存在购物车
        String key = KEY_PREFIX + user.getId();
        if(!stringRedisTemplate.hasKey(key)){
            // 不存在，直接返回
            return null;
        }
        // 得到购物车
        BoundHashOperations<String, Object, Object> hashOps = stringRedisTemplate.boundHashOps(key);

        List<Object> carts = hashOps.values();
        // 判断是否有数据
        if(CollectionUtils.isEmpty(carts)){
            return null;
        }
        // 查询购物车数据
        return carts.stream().map(o -> JsonUtils.parse(o.toString(), Cart.class)).collect(Collectors.toList());
    }

    public void updateCart(Cart cart) {
        // 获取登陆信息
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        String key = KEY_PREFIX + userInfo.getId();
        // 获取hash操作对象
        BoundHashOperations<String, Object, Object> hashOperations = stringRedisTemplate.boundHashOps(key);
        // 获取购物车信息
        String cartJson = hashOperations.get(cart.getSkuId().toString()).toString();
        Cart cart1 = JsonUtils.parse(cartJson, Cart.class);
        // 更新数量
        cart1.setNum(cart.getNum());
        // 写入购物车
        hashOperations.put(cart.getSkuId().toString(), JsonUtils.serialize(cart1));
    }

    public void deleteCart(String skuId) {
        // 获取登录用户
        UserInfo user = LoginInterceptor.getLoginUser();
        String key = KEY_PREFIX + user.getId();
        BoundHashOperations<String, Object, Object> hashOps = stringRedisTemplate.boundHashOps(key);
        hashOps.delete(skuId);
    }
}
