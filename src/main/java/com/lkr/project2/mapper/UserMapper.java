package com.lkr.project2.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.lkr.project2.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM sys_user WHERE id = #{id}")
    User selectById(@Param("id") Long id);
    
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);
    
    @Insert("INSERT INTO sys_user (username, password) VALUES (#{username}, #{password})")
    void insert(User user);
    
    // 手动实现分页查询
    @Select("SELECT * FROM sys_user LIMIT #{pageSize} OFFSET #{offset}")
    List<User> selectPage(@Param("pageSize") Integer pageSize, @Param("offset") Integer offset);
    
    // 查询总记录数
    @Select("SELECT COUNT(*) FROM sys_user")
    Long selectCount();
}