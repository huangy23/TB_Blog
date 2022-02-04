package com.treebee.blog.controller;

import com.treebee.blog.entity.Article;
import com.treebee.blog.entity.BlogComment;
import com.treebee.blog.service.BlogArticleService;
import com.treebee.blog.common.Constants;
import com.treebee.blog.service.CommentService;
import com.treebee.blog.service.SysLogService;
import com.treebee.blog.util.PageResult;
import com.treebee.blog.util.Result;
import com.treebee.blog.util.PageUtil;
import com.treebee.blog.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Controller
public class BlogArticleController {

    @Resource
    private BlogArticleService blogArticleService;

    @Resource
    private SysLogService sysLogService;

    @Resource
    private CommentService commentService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @RequestMapping({"/users/{pageNum}"})
    public String page(@RequestParam Map<String, Object> params,HttpServletRequest request, @PathVariable("pageNum") int pageNum) {
        PageUtil pageUtil = new PageUtil(params);
        PageResult blogPageResult = (PageResult) ResultGenerator.genSuccessResult(blogArticleService.getAdminUserPage(pageUtil)).getData();
        if (blogPageResult == null) {
            return "error/error_404";
        }
        request.setAttribute("blogPageResult", blogPageResult);

        request.setAttribute("pageName", "首页");
        return "yum_index";
    }


    @RequestMapping(value = "/users/list", method = RequestMethod.GET)
    public String list(@RequestParam Map<String, Object> params, ModelMap mmap, Model model) {
        PageUtil pageUtil = new PageUtil(params);
        Result temp = ResultGenerator.genSuccessResult(blogArticleService.getAdminUserPage(pageUtil));
        PageResult pageResult = (PageResult) temp.getData();
        int current_page = pageResult.getCurrPage();
        String next_page = null;
        String previous_page = null;
        if (current_page-1 == 0 && current_page != pageResult.getTotalPage()){
            next_page = "/users/list?page="+(current_page+1)+"&limit="+pageUtil.getLimit();
        }
        if (current_page-1 != 0 && current_page == pageResult.getTotalPage()){
            previous_page = "/users/list?page="+(current_page-1)+"&limit="+pageUtil.getLimit();
        }
        if (current_page-1 != 0 && current_page != pageResult.getTotalPage()){
            next_page = "/users/list?page="+(current_page+1)+"&limit="+pageUtil.getLimit();
            previous_page = "/users/list?page="+(current_page-1)+"&limit="+pageUtil.getLimit();
        }
        model.addAttribute("next_p",next_page);
        model.addAttribute("previous_p",previous_page);
        List<Article> temp2List = (List<Article>) pageResult.getList();

        List<Article> PharseAtc = new ArrayList<>();
        List<String> PharseLink = new ArrayList<>();
        int i = 0;
        for (Article atc : temp2List){

            int temp_id = Math.toIntExact(atc.getId());
            String new_string = "/users/html/"+temp_id+".html";
            PharseLink.add(new_string);
            atc.setLink(new_string);
            PharseAtc.add(atc);
            i++;
        }
        mmap.put("Article",PharseAtc);
        mmap.put("Link",PharseLink);
//        return "aaa";
        return "yum_index";
    }

    @RequestMapping(value = "/admin/users/list_o", method = RequestMethod.GET)
    @ResponseBody
    public Result list_o(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "参数异常！");
        }
        //查询列表数据
        PageUtil pageUtil = new PageUtil(params);
        Result temp = ResultGenerator.genSuccessResult(blogArticleService.getAdminUserPage(pageUtil));
        return temp;
    }


    @RequestMapping(value = "/users/html/{id}.html", method = RequestMethod.GET)
    public String getNewsWebPageById(HttpServletRequest request, @PathVariable("id") int id,
                                     @RequestParam(value = "commentPage", required = false, defaultValue = "1") Integer commentPage) {
        Article article = blogArticleService.findArticleById(id);
        request.setAttribute("article",article);
        PageResult comment_pageresult = commentService.getCommentPageByBlogIdAndPageNum((long) id,commentPage);
        if(comment_pageresult != null){
            List<BlogComment> list_comment = (List<BlogComment>) comment_pageresult.getList();
            List<BlogComment> list_temp = new ArrayList<>();
            for (BlogComment comment : list_comment)
            {
                if (comment.getBlogId() == id && comment.getIsDeleted() ==0 && comment.getCommentStatus() == 1){
                    list_temp.add(comment);
                }
            }
            comment_pageresult.setList(list_temp);
            request.setAttribute("commentPageResult",comment_pageresult);
        }


//        return "blog/blog_detail";
        return "blog/yum_detail";
    }

    @RequestMapping(value = "/admin/users/list_syslog", method = RequestMethod.GET)
    @ResponseBody
    public Result list_syslog(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "参数异常！");
        }
        //查询列表数据
        PageUtil pageUtil = new PageUtil(params);
        Result temp = ResultGenerator.genSuccessResult(sysLogService.getSysLogPage(pageUtil));
        return temp;
    }
}