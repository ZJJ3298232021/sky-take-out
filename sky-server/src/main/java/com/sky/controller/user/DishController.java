package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "C端-菜品浏览接口")
public class DishController {
    private final DishService dishService;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId categoryId
     * @return ...
     * @Cache description 根据分类ID查询菜品，首先检查缓存，如果存在分类键值那就取出菜品数据，没有则缓存
     */
    @Cacheable(value = "dish", key = "#categoryId", unless = "#result == null")
    @GetMapping("/list")
    @Operation(description = "根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        log.info("C端-根据分类id查询菜品,分类ID:{}", categoryId);

        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        return Result.success(dishService.listWithFlavor(dish));
    }

}
