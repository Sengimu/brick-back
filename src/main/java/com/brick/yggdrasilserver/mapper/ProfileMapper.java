package com.brick.yggdrasilserver.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProfileMapper {

    @Select("select * from profile where user_id = #{userId}")
    List<Map<String, Object>> selectProfilesByUserId(int userId);

    @Select("select * from profile where id = #{id}")
    Map<String, Object> selectProfileById(String id);

    @Select("select * from texture where id = #{id}")
    List<Map<String, Object>> selectTexturesById(String id);

    @Select("select * from profile where name = #{name}")
    Map<String, Object> selectProfileByProfileName(String name);

    @Select("select count(*) from texture where id = #{id}")
    int hasProfileTexture(String id);

    @Delete("delete from texture where id = #{id}")
    int deleteById(String id);

    @Update("update texture set filename = 'steven' where id = #{id}")
    int deleteTextureById(String id);

    @Insert("insert into texture (id,name,type,timestamp,filename,model) values (#{id},#{name},#{type},#{timestamp},#{filename},#{model})")
    int putTexture(String id, String name, String type, long timestamp, String filename, String model);

    @Select("select * from texture where name = #{name}")
    List<Map<String, Object>> selectTextureByName(String name);
}
