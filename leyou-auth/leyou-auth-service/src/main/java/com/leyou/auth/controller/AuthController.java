package com.leyou.auth.controller;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录授权
     * @param username
     * @param password
     * @param request
     * @param response
     * @return
     */
    @PostMapping("accredit")
    public ResponseEntity<Void> authentication
            (@RequestParam("username")String username,
             @RequestParam("password")String password,
             HttpServletRequest request,
             HttpServletResponse response){

        String token = authService.authentication(username,password);
        if (token == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),
                token,jwtProperties.getCookieMaxAge() * 60,true);
        return ResponseEntity.ok().build();
    }

    /**
     * 验证登录信息
     * @param token
     * @return
     */
    @RequestMapping("verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("LY_TOKEN") String token,
                                           HttpServletRequest request,
                                           HttpServletResponse response){

        try {
            // 从token中解析useInfo
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());

            // 刷新cookie
            token = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());

            // 设置cookie
            CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),
                    token,jwtProperties.getCookieMaxAge() * 60,true);

            // 解析成功返回user
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
