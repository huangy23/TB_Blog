package com.treebee.blog.dao;

import com.treebee.blog.entity.AdminUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AdminUserMapper {

    @Select("select * from tb_newbee_mall_admin_user where " +
            "login_user_name = #{userName,jdbcType=VARCHAR} " +
            "AND login_password=#{password,jdbcType=VARCHAR} " +
            "AND locked = 0")
    AdminUser login(@Param("userName") String userName, @Param("password") String password);

    @Select("select * from tb_newbee_mall_admin_user where admin_user_id = #{adminUserId,jdbcType=INTEGER}")
    AdminUser selectByPrimaryKey(Integer loginUserId);

    @Update("update tb_newbee_mall_admin_user set login_user_name = #{loginUserName,jdbcType=VARCHAR}, " +
            "login_password = #{loginPassword,jdbcType=VARCHAR}, " +
            "nick_name = #{nickName,jdbcType=VARCHAR}, " +
            "locked = #{locked,jdbcType=TINYINT} " +
            "where admin_user_id = #{adminUserId,jdbcType=INTEGER}")
    int updateByPrimaryKeySelective(AdminUser adminUser);
}
