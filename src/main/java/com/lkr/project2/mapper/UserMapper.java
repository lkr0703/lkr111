package com.lkr.project2.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.lkr.project2.entity.User;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM sys_user WHERE id = #{id}")
    User selectById(@Param("id") Long id);
    
    @Select("SELECT * FROM sys_user WHERE username = #{username, jdbcType=VARCHAR}")
    User selectByUsername(@Param("username") String username);
    
    @Insert("INSERT INTO sys_user (username, password) VALUES (#{username, jdbcType=VARCHAR}, #{password, jdbcType=VARCHAR})")
    void insert(User user);
}