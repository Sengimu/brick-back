package com.brick.yggdrasilserver.mapper;

import com.brick.yggdrasilserver.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface WebUserMapper {

    @Select("select count(*) from user where username = #{username}")
    int exist(String username);

    @Insert("insert into user (username,password) values (#{username},#{password})")
    int register(String username, String password);

    @Select("select id from user where username = #{username}")
    int selectUserIdByUsername(String username);

    @Insert("insert into user_property (name,value,user_id) values ('preferredLanguage','zh_CN',#{id})")
    boolean registerUserProperty(int id);
}
