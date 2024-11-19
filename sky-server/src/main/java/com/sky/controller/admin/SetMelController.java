package com.sky.controller.admin;

import com.sky.constant.PathConstant;
import com.sky.dto.SetmealDTO;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     *
     */
}
