package com.treebee.blog.service;

import com.treebee.blog.dao.SysLogDao;
import com.treebee.blog.entity.SysLog;
import com.treebee.blog.util.PageResult;
import com.treebee.blog.util.PageUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysLogService {
    @Resource
    private SysLogDao sysLogDao;

    public int add_log(SysLog sysLog){
        //int id = sysLog.getId();
        String username = sysLog.getUsername();
        String operation = sysLog.getOperation();
        int time = sysLog.getTime();
        String method = sysLog.getParams();
        String params = sysLog.getParams();
        String ip = sysLog.getIp();
        String create_time = (sysLog.getCreateTime());

        return sysLogDao.add_log(username, operation, time, method, params, ip, create_time);
    }

    public PageResult getSysLogPage(PageUtil pageUtil) {
        //当前页码中的数据列表
        List<SysLog> sysLogs = sysLogDao.findLogs(pageUtil);
        //数据总条数 用于计算分页数据
        int total = sysLogDao.getTotalLog(pageUtil);
        PageResult pageResult = new PageResult(sysLogs, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
