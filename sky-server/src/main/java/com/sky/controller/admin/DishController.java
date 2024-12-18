package com.sky.controller.admin;

import com.sky.constant.PathConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@RestController
@Slf4j
@Tag(name = "菜品管理")
@RequestMapping(PathConstant.ADMIN_DISH)
@RequiredArgsConstructor
@SuppressWarnings("all")
public class DishController {
    private final DishService dishService;

    /*
     * 新增菜品
     * @param dish
     * @return Result
     */
    @PostMapping
    @Operation(description = "新增菜品")
    public Result addDish(@RequestBody DishDTO dish) {
        log.info("新增菜品，菜品数据：{}", dish);
        dishService.insertDishWithFlavor(dish);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dto
     * @return Result
     */
    @GetMapping("/page")
    @Operation(description = "菜品分页查询")
    public Result<PageResult<DishVO>> dishPage(DishPageQueryDTO dto) {
        log.info("菜品分页查询:{}", dto);
        PageResult<DishVO> result = dishService.pageQuery(dto);
        return Result.success(result);
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    @Operation(description = "批量删除菜品")
    public Result dishDelete(@RequestParam List<Long> ids) {
        log.info("批量删除菜品，id为{}", ids);
        dishService.deleteBatches(ids);
        return Result.success();
    }

    @PutMapping
    @Operation(description = "修改菜品")
    public Result dishUpdate(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品，菜品数据为：{}", dishDTO);
        dishService.updateDishWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 根据ID查询菜品
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @Operation(description = "根据ID查询菜品")
    public Result<DishVO> getDishById(@PathVariable Long id) {
        log.info("根据ID查询菜品，id为{}", id);
        DishVO dish = dishService.getByIdWithFlavor(id);
        return Result.success(dish);
    }

    /**
     * 启用禁用菜品
     * @param status 设置状态
     * @param id 菜品id
     * @return Result
     */
    @PostMapping("/status/{status}")
    @Operation(description = "启用禁用菜品")
    public Result dishStartOrStop(@PathVariable("status") Integer status, Long id) {
        log.info("启用禁用菜品，状态为{}，id为{}", status, id);
        dishService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据分类id或名称查询菜品
     * @param categoryId 分类id
     * @return Result
     */
    @GetMapping("/list")
    @Operation(description = "根据分类id或名称查询菜品")
    public Result<List<Dish>> dishList(Long categoryId,String name) {
        log.info("根据分类id或名称查询菜品，分类id为{}，名称为{}", categoryId, name);
        List<Dish> dishList = dishService.list(categoryId,name);
        return Result.success(dishList);
    }
}
