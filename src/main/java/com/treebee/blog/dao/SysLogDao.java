package com.treebee.blog.dao;

import com.treebee.blog.entity.SysLog;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SysLogDao {

    @Insert("insert into sys_log(username,operation,time,method,params,ip,create_time) " +
            "values (#{username},#{operation},#{time},#{method}," +
            "#{params},#{ip},#{create_time})")
    int add_log(@Param("username") String username,
                @Param("operation") String operation,
                @Param("time") int time,
                @Param("method") String method,
                @Param("params") String params,
                @Param("ip") String ip,
                @Param("create_time") String create_time);

    @Select("select id,username,operation,time,method,params,ip,create_time from sys_log order by id DESC limit #{start},#{limit}")
    List<SysLog> findLogs(Map param);

    @Select("select count(*) from sys_log")
    int getTotalLog(Map param);
}
