package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     *
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 保存员工信息到数据库
     *
     * @param employee 要保存的员工对象，包含员工的所有信息
     */
    @AutoFill(OperationType.INSERT)
    void save(Employee employee);

    /**
     * 查询员工分页数据
     * <p>
     * 本方法用于根据提供的查询条件分页获取员工数据，通过EmployeePageQueryDTO参数来筛选员工信息
     * 主要用于支持前端展示分页数据，提高系统响应效率和用户体验
     *
     * @param pageQuery 包含分页查询条件的DTO对象，用于指定查询条件如页码、每页数量等
     * @return 返回一个Page对象，包含分页显示的员工数据列表和分页信息，如总记录数、当前页码等
     */
    Page<Employee> queryPage(EmployeePageQueryDTO pageQuery);

    /**
     * 根据id修改员工信息
     *
     * @param employee 要修改的员工对象，包含员工ID和其他属性
     */
    @AutoFill(OperationType.UPDATE)
    void update(Employee employee);

    /**
     * 根据id查询员工信息
     *
     * @param id 员工ID
     * @return 返回员工对象，包含员工所有信息
     */
    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);
}
