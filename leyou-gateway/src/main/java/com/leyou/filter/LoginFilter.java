package com.leyou.filter;

import com.leyou.auth.utils.JwtUtils;
import com.leyou.config.FilterProperties;
import com.leyou.config.JwtProperties;
import com.leyou.utils.CookieUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private FilterProperties filterProperties;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        // 获得上下文
        RequestContext context = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest request = context.getRequest();
        // 获取路径
        String uri = request.getRequestURI();
        // 判断白名单
        List<String> allowPaths = filterProperties.getAllowPaths();
        for (String allowPath : allowPaths) {
            // 然后判断是否是符合
            if(uri.startsWith(allowPath)){
                return false;
            }
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        // 获得上下文
        RequestContext context = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest request = context.getRequest();
        //获取token
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        try {
            // 验证通过就放行
            JwtUtils.getInfoFromToken(token,jwtProperties.getPublicKey());
        }catch (Exception e){
            // 校验出现异常，返回403
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
        }

        // 返回null即为放行
        return null;
    }
}
