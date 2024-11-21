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
import org.springframework.web.bind.annotation.*;

/**
 * 套餐管理
 * @author zank
 */
@RestController
@RequestMapping(PathConstant.ADMIN_SETMEAL)
@Slf4j
@Tag(name = "套餐相关接口")
@RequiredArgsConstructor
public class SetMelController {

    private final SetMealService setMealService;

    /**
     * 新增套餐
     * @param setmealDTO
     * @return result
     */
    @Operation(description = "新增套餐")
    @PostMapping
    public Result saveSetMeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐：{}", setmealDTO);
        setMealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Operation(description = "根据id查询套餐")
    public Result<SetmealVO> getSetMealById(@PathVariable Long id) {
        log.info("根据id查询套餐：{}", id);
        return Result.success(setMealService.getByIdWithDish(id));
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @Operation(description = "套餐分页查询")
    public Result<PageResult<SetmealVO>> pageQuerySetMeal(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询：{}", setmealPageQueryDTO);
        return Result.success(setMealService.pageQuery(setmealPageQueryDTO));
    }

    /**
     * 启用禁用套餐
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @Operation(description = "启用禁用套餐")
    public Result startOrStopSetMeal(@PathVariable Integer status, Long id) {
        log.info("启用禁用套餐：{}", status);
        setMealService.startOrStop(status, id);
        return Result.success();
    }
}
