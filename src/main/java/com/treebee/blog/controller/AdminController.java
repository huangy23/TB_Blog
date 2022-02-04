package com.treebee.blog.controller;

import com.treebee.blog.annotation.Log;
import com.treebee.blog.entity.AdminUser;
import com.treebee.blog.entity.Article;
import com.treebee.blog.entity.CategoryStat;
import com.treebee.blog.service.AdminUserService;
import com.treebee.blog.service.BlogArticleService;
import com.treebee.blog.util.Result;
import com.treebee.blog.util.ResultGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wf.captcha.ArithmeticCaptcha;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class AdminController {


    @Resource
    private AdminUserService adminUserService;

    @Resource
    private BlogArticleService blogArticleService;

    @GetMapping("/kaptcha")
    public void Kaptcha(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/png");

        // 算术类型
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(150, 40);
        captcha.setLen(2);  // 几位数运算，默认是两位
        captcha.getArithmeticString();  // 获取运算的公式：3+2=?
        captcha.text();  // 获取运算的结果：5
        httpServletRequest.getSession().setAttribute("verifyCode", captcha.text().toLowerCase());
        captcha.out(httpServletResponse.getOutputStream());  // 输出验证码
    }

    @GetMapping("/")
    public String home(){
        return "redirect:/users/list?page=1&limit=10";
    }

    @GetMapping("/admin/errorpage")
    public String errorpage(){
        return "admin/errorpage";
    }

    @GetMapping("/admin/article_list.html")
    public String articleList() {
        return "admin/article_list";
    }

    @GetMapping("/admin/syslog.html")
    public String syslog() {
        return "admin/syslog";
    }

    @GetMapping({"/admin/dashboard"})
    public String index(HttpServletRequest request) {
        request.setAttribute("path", "index");
        request.setAttribute("blogCount",blogArticleService.getTotalId());

        return "admin/dashboard.html";
    }

    @Log("admin/article")
    @GetMapping("/admin/article")
    public String article(Model model) {
        model.addAttribute("article",new Article());
        return "admin/article";
    }

    @Log("admin/article")
    @PostMapping("/admin/article")
    public String article() {
        return "admin/article";
    }


    @Log("admin/uploadcontent")
    @PostMapping("/admin/uploadcontent")
    public String uploadContent(@ModelAttribute Article article) throws JsonProcessingException {
        int id = blogArticleService.getLastId() + 1;
        String tempCategory = article.getCategory();
        DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        Date today=new Date();
        String pubtime =df.format(today);
        String url = "fffff";

        //fangzai article li
        Article temp_article = new Article();
        temp_article.setId((long) id);
        temp_article.setTitle(article.getTitle());
        temp_article.setPubtime(pubtime);
        temp_article.setLink(url);
        temp_article.setContent(article.getContent());
        temp_article.setCategory(tempCategory);

        int t = blogArticleService.add(temp_article,id);
        if(t==1){
            return "redirect:/users/html/"+id+".html";
        }else {

            return "admin/article";
        }
    }

    @Log("/admin/getCategoryList")
    @GetMapping("/admin/getCategoryList")
    @ResponseBody
    public List<CategoryStat> getCategoryList(){
        return blogArticleService.getCategoryStat();
    }

    @Log("admin/profile")
    @GetMapping("/admin/profile")
    public String profile(HttpServletRequest request) {
        Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
        AdminUser adminUser = adminUserService.getUserDetailById(loginUserId);
        if (adminUser == null) {
            return "admin/dashboard";
        }
        request.setAttribute("path", "profile");
        request.setAttribute("loginUserName", adminUser.getLoginUserName());
        request.setAttribute("nickName", adminUser.getNickName());
        return "admin/profile";
    }

    @Log("admin/profile/password")
    @PostMapping("/admin/profile/password")
    @ResponseBody
    public String passwordUpdate(HttpServletRequest request, @RequestParam("originalPassword") String originalPassword,
                                 @RequestParam("newPassword") String newPassword) {
        if (StringUtils.isEmpty(originalPassword) || StringUtils.isEmpty(newPassword)) {
            return "参数不能为空";
        }
        Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
        if (adminUserService.updatePassword(loginUserId, originalPassword, newPassword)) {
            //修改成功后清空session中的数据，前端控制跳转至登录页
            request.getSession().removeAttribute("loginUserId");
            request.getSession().removeAttribute("loginUser");
            request.getSession().removeAttribute("errorMsg");
            return "success";
        } else {
            return "修改失败";
        }
    }

    @Log("admin/profile/name")
    @PostMapping("/admin/profile/name")
    @ResponseBody
    public String nameUpdate(HttpServletRequest request, @RequestParam("loginUserName") String loginUserName,
                             @RequestParam("nickName") String nickName) {
        if (StringUtils.isEmpty(loginUserName) || StringUtils.isEmpty(nickName)) {
            return "参数不能为空";
        }
        Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
        if (adminUserService.updateName(loginUserId, loginUserName, nickName)) {
            return "success";
        } else {
            return "修改失败";
        }
    }


    @GetMapping("/admin/login.html")
    public String login() {
        return "admin/login";
    }

    @PostMapping(value = "/admin/login")
    public String login(@RequestParam("userName") String userName,
                        @RequestParam("password") String password,
                        @RequestParam("verifyCode") String verifyCode,
                        HttpSession session) {
        if (StringUtils.isEmpty(verifyCode)) {
            session.setAttribute("errorMsg", "验证码不能为空");
            return "admin/login";
        }
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            session.setAttribute("errorMsg", "用户名或密码不能为空");
            return "admin/login";
        }
        String kaptchaCode = session.getAttribute("verifyCode") + "";
        if (StringUtils.isEmpty(kaptchaCode) || !verifyCode.equals(kaptchaCode)) {
            session.setAttribute("errorMsg", "验证码错误");
            return "admin/login";
        }
        AdminUser adminUser = adminUserService.login(userName, password);
        if (adminUser != null) {
            session.setAttribute("loginUser", adminUser.getNickName());
            session.setAttribute("loginUserId", adminUser.getAdminUserId());
            return "redirect:/admin/profile";
        } else {
            session.setAttribute("errorMsg", "登录信息错误");
            return "admin/login";
        }
    }

    @GetMapping("/admin/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute("loginUserId");
        request.getSession().removeAttribute("loginUser");
        request.getSession().removeAttribute("errorMsg");
        return "admin/login";
    }

    @Log("admin/article/delete")
    @RequestMapping(value = "/admin/article/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (adminUserService.deleteBatch(ids)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }

    @Log("admin/article/edit")
    @RequestMapping(value = "/admin/article/edit", method = RequestMethod.POST)
    @ResponseBody
    public Result edit(@RequestBody Article newArticle) {
        String title = newArticle.getTitle();
        Integer ids = Math.toIntExact(newArticle.getId());
        if (adminUserService.editBatch(title,ids)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }
}