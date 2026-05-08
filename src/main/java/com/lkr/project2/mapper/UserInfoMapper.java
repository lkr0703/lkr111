package com.lkr.project2.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.lkr.project2.entity.UserInfo;
import com.lkr.project2.vo.UserDetailVO;

import java.util.List;

@Mapper
public interface UserInfoMapper {
    @Select("""
            SELECT 
                u.id AS userId,
                u.username,
                i.real_name AS realName,
                i.phone,
                i.address
            FROM sys_user u
            LEFT JOIN user_info i ON u.id = i.user_id
            WHERE u.id = #{userId}
            """)
    UserDetailVO getUserDetail(@Param("userId") Long userId);

    // 插入用户扩展信息
    @Select("INSERT INTO user_info (real_name, phone, address, user_id) VALUES (#{realName}, #{phone}, #{address}, #{userId})")
    void insert(UserInfo userInfo);

    // 更新用户扩展信息
    @Select("UPDATE user_info SET real_name = #{realName}, phone = #{phone}, address = #{address} WHERE user_id = #{userId}")
    void updateByUserId(UserInfo userInfo);

    // 删除用户扩展信息
    @Select("DELETE FROM user_info WHERE user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);

    // 根据用户ID查询扩展信息
    @Select("SELECT * FROM user_info WHERE user_id = #{userId}")
    UserInfo selectByUserId(@Param("userId") Long userId);

    // 查询所有用户详情（分页）
    @Select("""
            SELECT 
                u.id AS userId,
                u.username,
                i.real_name AS realName,
                i.phone,
                i.address
            FROM sys_user u
            LEFT JOIN user_info i ON u.id = i.user_id
            LIMIT #{pageSize} OFFSET #{offset}
            """)
    List<UserDetailVO> selectUserDetailPage(@Param("pageSize") Integer pageSize, @Param("offset") Integer offset);

    // 查询用户详情总数
    @Select("SELECT COUNT(*) FROM sys_user")
    Long selectUserDetailCount();
}