package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     *
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 分页查询员工信息
     *
     * @param pageQuery 员工分页查询条件DTO
     * @return 返回员工信息的分页结果
     */
    PageResult<Employee> pageQuery(EmployeePageQueryDTO pageQuery);

    /**
     * 启用或停用账号
     *
     * @param status 账号状态，1表示启动，0表示停用
     * @param id     账号ID
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据ID查询员工信息
     *
     * @param id 员工ID
     * @return 返回员工信息
     */
    Employee getById(Long id);

    /**
     * 修改员工信息
     *
     * @param employeeDTO 员工信息DTO
     */
    void update(EmployeeDTO employeeDTO);
}
