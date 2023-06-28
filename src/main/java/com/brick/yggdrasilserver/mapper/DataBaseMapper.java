package com.brick.yggdrasilserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DataBaseMapper {

    @Select("select count(*) from information_schema.TABLES where TABLE_SCHEMA =  #{tableSchema};")
    int getTables(String tableSchema);

    int insertTest();

    boolean createTable();
}
