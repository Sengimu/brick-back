package com.brick.yggdrasilserver.mapper;

import com.brick.yggdrasilserver.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    @Select("select name,value from user_property where user_id = #{id}")
    List<Map<String, Object>> getUserProperties(int id);

    @Select("select id,username from user where username = #{username} and password = #{password}")
    User selectUserByUsernameAndPassword(String username, String password);

    @Select("select user_id from profile where id = #{id}")
    int selectUserIdById(String id);

    @Select("select * from user where id = #{id}")
    User selectUserByUserId(int id);
}
