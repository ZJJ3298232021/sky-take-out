package com.sky.interceptor;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

/**
 * 管理员jwt令牌校验的拦截器
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;
    private final Gson gson;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 校验jwt
     *
     * @param request  .
     * @param response .
     * @param handler  .
     * @return .
     * @throws Exception .
     */
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getAdminTokenName());

        //2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
            log.info("当前员工id：{}", empId);
            //保存当前登录用户的id
            BaseContext.setCurrentId(empId);
            String storedToken = stringRedisTemplate.opsForValue().get("sky-take-out:emp::" + empId);
            //如果redis中没有这个token，或者token不匹配，则返回登录失败
            if (storedToken != null) {
                Result<LinkedTreeMap<?, ?>> result = gson.fromJson(storedToken, Result.class);
                if (!Objects.equals(result.getData().get("token"), token))
                    failed(response,0);
            }
            //3、通过，放行
            return true;
        } catch (Exception ex) {
            return failed(response,1);
        }
    }


    /**
     * 登录失败
     *
     * @param response .
     * @return .
     * @throws Exception .
     */
    private boolean failed(HttpServletResponse response,int type) throws Exception {
        //4、不通过，响应状态码401
        response.setStatus(401);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(gson.toJson(Result.error(Objects.equals(type,1)?MessageConstant.USER_NOT_LOGIN:MessageConstant.RETRY_LOGIN)));
        return false;
    }
}
