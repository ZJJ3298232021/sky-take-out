package com.sky.controller.user;

import com.sky.constant.PathConstant;
import com.sky.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * 用户端店铺相关接口
 */
@RestController("userShopController")
@RequestMapping(PathConstant.USER_SHOP)
@Tag(name = "店铺相关接口")
@Slf4j
@RequiredArgsConstructor
public class ShopController {
    private static final String KEY = "sky-take-out:shop:status";
    public final StringRedisTemplate redisTemplate;

    @GetMapping("/status")
    @Operation(description = "获取店铺营业状态")
    public Result<Integer> userGetStatus() {
        String status = redisTemplate.opsForValue().get(KEY);
        if (status != null) {
            log.info("获取店铺营业状态：{}", status.equals("1") ? "营业" : "打样");
            return Result.success(Integer.parseInt(status));
        }
        redisTemplate.opsForValue().set(KEY, "0");
        return Result.error("获取失败");
    }
}
