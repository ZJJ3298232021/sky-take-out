package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Tag(name = "员工管理")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @Operation(description = "员工登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> empLogin(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @Operation(description = "员工退出")
    @PostMapping("/logout")
    public Result<String> empLogout() {
        return Result.success();
    }

    /**
     * 新增员工信息
     *
     * @param employeeDTO 员工数据传输对象，包含员工的详细信息
     * @return Result 返回操作结果，包含成功与否的信息
     */
    @PostMapping
    @Operation(description = "新增员工")
    public Result empSave(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工，员工数据：{}", employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 员工分页查询
     *
     * @param pageQuery 分页查询参数对象，包含查询条件和分页信息
     * @return 返回分页查询结果的封装对象
     */
    @GetMapping("/page")
    @Operation(description = "员工分页查询")
    public Result<PageResult<Employee>> empPage(EmployeePageQueryDTO pageQuery) {
        log.info("员工分页查询，参数：{}", pageQuery);
        PageResult<Employee> pageResult = employeeService.pageQuery(pageQuery);
        return Result.success(pageResult);
    }

    /**
     * 启用或禁用员工账号
     * 该方法接收一个状态参数和一个员工ID，根据状态来启用或禁用员工账号
     *
     * @param status 员工账号的状态，用于判断是启用还是禁用
     * @param id 员工的ID，用于标识需要操作的员工
     * @return 返回一个成功的结果对象，表示操作成功
     */
    @PostMapping("/status/{status}")
    @Operation(description = "启用禁用员工账号")
    public Result empStartOrStop(@PathVariable Integer status, Long id) {
        log.info("启用禁用员工账号：status:{} , id:{}", status, id);
        employeeService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据id查询员工信息
     * @param id 员工id
     * @return 返回员工信息
     */
    @GetMapping("/{id}")
    @Operation(description = "根据id查询员工")
    public Result<Employee> empGetById(@PathVariable Long id) {
        log.info("根据id查询员工：{}", id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    @PutMapping
    @Operation(description = "修改员工信息")
    public Result empUpdate(@RequestBody EmployeeDTO employeeDTO) {
        log.info("修改员工信息：{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }
}
