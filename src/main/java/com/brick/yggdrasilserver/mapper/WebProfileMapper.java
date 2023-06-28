package com.brick.yggdrasilserver.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WebProfileMapper {

    @Select("select filename from texture where id = #{id}")
    String getFilenameById(String id);

    @Select("select count(1) from profile where user_id = #{userId}")
    int selectUserProfile(int userId);

    @Select("select count(1) from profile where id = #{id}")
    int selectExistProfileByUserId(String id);

    @Insert("insert into profile values (#{id},#{name},#{userId})")
    boolean createProfile(String id, String name, int userId);
}
