package com.sky.controller.admin;

import com.sky.constant.PathConstant;
import com.sky.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping(PathConstant.ADMIN_SHOP)
@Tag(name = "店铺相关接口")
@Slf4j
@RequiredArgsConstructor
public class ShopController {

    public final StringRedisTemplate redisTemplate;

    private static final String KEY = "sky-take-out:shop:status";


    @GetMapping("/status")
    @Operation(description = "获取店铺营业状态")
    public Result<Integer> adminGetStatus() {
        String status = redisTemplate.opsForValue().get(KEY);
        if (status != null) {
            log.info("获取店铺营业状态：{}", status.equals("1")? "营业":"打样");
            return Result.success(Integer.parseInt(status));
        }
        return Result.error("获取失败");
    }


    @PutMapping("/{status}")
    @Operation(description = "修改店铺状态")
    public Result<?> changeStatus(@PathVariable Integer status) {
        log.info("修改店铺状态:{}", status == 0 ? "打样" : "营业");
        redisTemplate.opsForValue().set(KEY,status.toString());
        return Result.success();
    }

}
