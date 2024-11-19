package com.sky.controller.admin;

import com.sky.constant.PathConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@RestController
@RequestMapping(PathConstant.ADMIN_CATEGORY)
@Tag(name = "分类相关接口")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     *
     * @param categoryDTO categoryDTO
     * @return Result<String>
     */
    @PostMapping
    @Operation(description = "新增分类")
    public Result<String> categorySave(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类：{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    /**
     * 分类分页查询
     *
     * @param categoryPageQueryDTO categoryPageQueryDTO
     * @return Result<PageResult>
     */
    @GetMapping("/page")
    @Operation(description = "分类分页查询")
    public Result<PageResult> categoryPage(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分页查询：{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除分类
     *
     * @param id 要删除的分类id
     * @return Result<String>
     */
    @DeleteMapping
    @Operation(description = "删除分类")
    public Result<String> categoryDeleteById(Long id) {
        log.info("删除分类：{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改分类
     *
     * @param categoryDTO categoryDTO
     * @return Result<String>
     */
    @PutMapping
    @Operation(description = "修改分类")
    public Result<String> categoryUpdate(@RequestBody CategoryDTO categoryDTO) {
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     * 启用、禁用分类
     *
     * @param status 状态
     * @param id 分类id
     * @return Result<String>
     */
    @PostMapping("/status/{status}")
    @Operation(description = "启用禁用分类")
    public Result<String> categoryStartOrStop(@PathVariable("status") Integer status, Long id) {
        categoryService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据类型查询分类
     *
     * @param type 分类类型
     * @return Result<List<Category>>
     */
    @GetMapping("/list")
    @Operation(description = "根据类型查询分类")
    public Result<List<Category>> categoryList(Integer type) {
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
