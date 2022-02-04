package com.treebee.blog.service;

import com.treebee.blog.dao.AdminUserMapper;
import com.treebee.blog.dao.ArticleDao;
import com.treebee.blog.entity.AdminUser;
import com.treebee.blog.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@Service
public class AdminUserService {
    @Resource
    private AdminUserMapper adminUserMapper;

    @Resource
    private ArticleDao articleDao;

    @Autowired
    RedisTemplate redisTemplate;

    public AdminUser login(String userName, String password) {
        String passwordMd5 = MD5Util.MD5Encode(password, "UTF-8");
        return adminUserMapper.login(userName, passwordMd5);
    }

    public AdminUser getUserDetailById(Integer loginUserId) {
        return adminUserMapper.selectByPrimaryKey(loginUserId);
    }

    public Boolean updateName(Integer loginUserId, String loginUserName, String nickName) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(loginUserId);
        //当前用户非空才可以进行更改
        if (adminUser != null) {
            //设置新名称并修改
            adminUser.setLoginUserName(loginUserName);
            adminUser.setNickName(nickName);
            if (adminUserMapper.updateByPrimaryKeySelective(adminUser) > 0) {
                //修改成功则返回true
                return true;
            }
        }
        return false;
    }

    public Boolean updatePassword(Integer loginUserId, String originalPassword, String newPassword) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(loginUserId);
        //当前用户非空才可以进行更改
        if (adminUser != null) {
            String originalPasswordMd5 = MD5Util.MD5Encode(originalPassword, "UTF-8");
            String newPasswordMd5 = MD5Util.MD5Encode(newPassword, "UTF-8");
            //比较原密码是否正确
            if (originalPasswordMd5.equals(adminUser.getLoginPassword())) {
                //设置新密码并修改
                adminUser.setLoginPassword(newPasswordMd5);
                if (adminUserMapper.updateByPrimaryKeySelective(adminUser) > 0) {
                    //修改成功则返回true
                    return true;
                }
            }
        }
        return false;
    }

    //可以用zset
    public Boolean deleteBatch(Integer[] ids) {
        if (ids.length < 1) {
            return false;
        }
        Jedis redis = new Jedis("127.0.0.1", 6379);

        for (int id : ids){
            //如果redis里有
            if (redis.zrangeByScore("z_article".getBytes(StandardCharsets.UTF_8),id-0.001,id+0.001).size() != 0){
                redis.zremrangeByScore("z_article",id-0.001, id+0.001);
            }
        }
        return articleDao.deleteBatch(ids) > 0;
    }

    public Boolean editBatch(String title,Integer ids) {
        Long id = ids.longValue();
        return articleDao.editBatch(title,id) > 0;
    }

}
