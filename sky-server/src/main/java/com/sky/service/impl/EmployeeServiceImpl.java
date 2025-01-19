package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.config.BcryptConfig;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final BcryptConfig bcryptConfig;

    private final EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO .
     * @return .
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        if (!BCrypt.checkpw(password, employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (Objects.equals(employee.getStatus(), StatusConstant.DISABLE)) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO .
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);

        //设置账号状态(因为前端没有传入，所以这里默认设置启用状态)
        employee.setStatus(StatusConstant.ENABLE);

        //密码加密处理
        employee.setPassword(
                BCrypt.hashpw(
                        PasswordConstant.DEFAULT_PASSWORD,
                        BCrypt.gensalt(
                                Integer.parseInt(
                                        bcryptConfig.getSalt()
                                )
                        )
                )
        );
        employeeMapper.save(employee);

    }

    /**
     * 分页查询员工列表
     * <p>
     * 本方法根据传入的分页查询参数，返回相应分页的员工列表
     * 主要用于处理员工管理相关的业务需求，通过提供分页查询功能，以支持大量数据的高效检索
     *
     * @param pageQuery 分页查询员工的名称，包含页码、每页数量等分页参数
     * @return 返回分页查询结果，包含当前页的员工列表以及总记录数等信息
     */
    @Override
    public PageResult<Employee> pageQuery(EmployeePageQueryDTO pageQuery) {
        PageHelper.startPage(pageQuery.getPage(), pageQuery.getPageSize());
        Page<Employee> employees = employeeMapper.queryPage(pageQuery);
        PageResult<Employee> pageResult = new PageResult<>();
        pageResult.setTotal(employees.getTotal());
        pageResult.setRecords(employees.getResult());
        return pageResult;
    }

    /**
     * 启用或停用账号
     *
     * @param status 账号状态，1表示启动，0表示停用
     * @param id     账号ID
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Employee employee = Employee
                .builder()
                .status(status)
                .id(id)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        employeeMapper.update(employee);
    }

    /**
     * 根据员工ID查询员工信息
     *
     * @param id 员工ID
     * @return 员工信息
     */
    @Override
    public Employee getById(Long id) {
        return employeeMapper.getById(id);
    }

    /**
     * 更新员工信息
     *
     * @param employeeDTO 员工信息DTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employeeMapper.update(employee);
    }
}
