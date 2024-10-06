package com.sky.mapper;

import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @Insert({
            "insert into employee (name, username, password, phone, sex,"+
            "id_number, status, create_time, update_time, create_user, update_user)"+
            "values (#{employee.name}, #{employee.username}, #{employee.password},"+
            "#{employee.phone}, #{employee.sex}, #{employee.idNumber},"+
            "#{employee.status}, #{employee.createTime}, #{employee.updateTime},"+
            "#{employee.createUser}, #{employee.updateUser})"
    })
    void save(@Param("employee") Employee employee);
}
