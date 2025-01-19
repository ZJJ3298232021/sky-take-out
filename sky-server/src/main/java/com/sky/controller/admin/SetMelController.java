package com.sky.controller.admin;

import com.sky.constant.PathConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 *
 * @author zank
 */
@RestController("adminSetMealController")
@RequestMapping(PathConstant.ADMIN_SETMEAL)
@Slf4j
@Tag(name = "套餐相关接口")
@RequiredArgsConstructor
public class SetMelController {

    private final SetMealService setMealService;

    /**
     * 新增套餐
     *
     * @param setmealDTO dto
     * @return result
     */
    @CacheEvict(value = "setmeal", key = "#setmealDTO.categoryId")
    @Operation(description = "新增套餐")
    @PostMapping
    public Result<?> saveSetMeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐：{}", setmealDTO);
        setMealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 根据id查询套餐
     *
     * @param id 套餐ID
     * @return result
     */
    @GetMapping("/{id}")
    @Operation(description = "根据id查询套餐")
    public Result<SetmealVO> getSetMealById(@PathVariable Long id) {
        log.info("根据id查询套餐：{}", id);
        return Result.success(setMealService.getByIdWithDish(id));
    }

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO 查询条件
     * @return PageResult
     */
    @GetMapping("/page")
    @Operation(description = "套餐分页查询")
    public Result<PageResult<SetmealVO>> pageQuerySetMeal(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询：{}", setmealPageQueryDTO);
        return Result.success(setMealService.pageQuery(setmealPageQueryDTO));
    }

    /**
     * 启用禁用套餐
     *
     * @param status 设置状态
     * @param id     套餐ID
     * @return result
     */
    @CacheEvict(value = "setmeal", allEntries = true)
    @PostMapping("/status/{status}")
    @Operation(description = "启用禁用套餐")
    public Result<?> startOrStopSetMeal(@PathVariable Integer status, Long id) {
        log.info("启用禁用套餐：{}", status);
        setMealService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 修改套餐
     *
     * @param setmealDTO dto
     * @return result
     */
    @CacheEvict(value = "setmeal", allEntries = true)
    @PutMapping
    @Operation(description = "修改套餐")
    public Result<?> updateSetMeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐：{}", setmealDTO);
        setMealService.updateSetMealWithDish(setmealDTO);
        return Result.success();
    }

    @CacheEvict(value = "setmeal", allEntries = true)
    @DeleteMapping
    @Operation(description = "批量删除套餐")
    public Result<?> deleteSetMeals(@RequestParam List<Long> ids) {
        log.info("批量删除套餐：{}", ids);
        setMealService.deleteBatchSetMeals(ids);
        return Result.success();
    }
}
