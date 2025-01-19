package com.sky.controller.user;

import com.sky.constant.PathConstant;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户购物车接口")
@Slf4j
@RestController
@RequestMapping(PathConstant.USER_SHOPPINGCART)
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     *
     * @param dto .
     * @return .
     */
    @Operation(description = "添加购物车")
    @PostMapping("/add")
    public Result<?> addToCart(@RequestBody ShoppingCartDTO dto) {
        log.info("添加购物车：{}", dto);
        shoppingCartService.addToCart(dto);
        return Result.success();
    }

    /**
     * 查看购物车
     *
     * @return .
     */
    @GetMapping("/list")
    @Operation(description = "查看购物车")
    public Result<List<ShoppingCart>> getCart() {
        log.info("查询购物车");
        List<ShoppingCart> items = shoppingCartService.getCart();
        return Result.success(items);
    }

    /**
     * 删除购物车
     *
     * @param dto .
     * @return .
     */
    @PostMapping("/sub")
    @Operation(description = "减少购物车菜品数量")
    public Result<?> subToCart(@RequestBody ShoppingCartDTO dto) {
        log.info("减少购物车：{}", dto);
        shoppingCartService.subToCart(dto);
        return Result.success();
    }

    /**
     * 清空购物车
     *
     * @return .
     */
    @DeleteMapping("/clean")
    @Operation(description = "清空购物车")
    public Result<?> cleanCart() {
        log.info("清空购物车");
        shoppingCartService.cleanCart();
        return Result.success();
    }
}
