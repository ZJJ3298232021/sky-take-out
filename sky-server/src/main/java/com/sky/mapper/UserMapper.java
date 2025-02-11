package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    /**
     * 根据openid查询用户
     *
     * @param openid .
     * @return .
     */
    @Select("select id, openid, name, phone, sex, id_number, avatar, create_time from user where openid = #{openid}")
    User getUserByOpenId(String openid);

    /**
     * 新增用户
     *
     * @param user .
     */
    void insert(User user);

    /**
     * 根据id查询用户
     *
     * @param id .
     * @return .
     */
    @Select("select id, openid, name, phone, sex, id_number, avatar, create_time from user where id = #{id}")
    User getById(Long id);

    /**
     * 根据日期查询总用户数
     * @param date .
     * @return .
     */
    Integer getTotalUser(String date);

    /**
     * 根据日期查询新增用户数
     * @param date .
     * @return .
     */
    Integer getNewUser(String date);
}
