package com.treebee.blog.service;

import com.treebee.blog.dao.ArticleDao;
import com.treebee.blog.entity.Article;
import com.treebee.blog.entity.CategoryStat;
import com.treebee.blog.tools.SerializeObjectTool;
import com.treebee.blog.util.PageResult;
import com.treebee.blog.util.PageUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class BlogArticleService {
    @Resource
    private ArticleDao articleDao;

    public PageResult getAdminUserPage(PageUtil pageUtil) {
        //当前页码中的数据列表
        List<Article> articles = articleDao.findUsers(pageUtil);
        //数据总条数 用于计算分页数据
        int total = articleDao.getTotalUser(pageUtil);
        PageResult pageResult = new PageResult(articles, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    public Article findArticleById(int id){
        Jedis redis = new Jedis("127.0.0.1", 6379);
        double lo = (double)id - 0.001;
        double hi = (double)id + 0.001;
        Set<byte[]> temp_byte_set = redis.zrangeByScore("z_article".getBytes(StandardCharsets.UTF_8),lo,hi);


        //查询缓存
        Article article;
        if (temp_byte_set.size() != 0){
            byte[] temp_byte = temp_byte_set.stream().findFirst().get();
            //byte[] byt = redis.get(String.valueOf(id).getBytes());
            Object obj = SerializeObjectTool.unserialize(temp_byte);
            article = (Article) obj;
            System.out.println("查询的是缓存");
        }else{
            article = articleDao.findArticleById(id);
            System.out.println("查询的是数据库");
        }

        return article;
    }

    public String findHtmlNews(int id){return articleDao.findHtmlNews(id);}

    public int add(Article article,int id) throws JsonProcessingException {
        Jedis redis = new Jedis("127.0.0.1", 6379);

        Set<Tuple> temp = redis.zrangeWithScores("z_article",0,0);

        redis.zadd("z_article".getBytes(StandardCharsets.UTF_8),(long) id,SerializeObjectTool.serialize(article));

        //redis.set(str_id.getBytes(StandardCharsets.UTF_8), SerializeObjectTool.serialize(article));

        //g while
        if (Math.toIntExact(redis.zcard("z_article")) > 10){
            Tuple temp_t = temp.stream().findFirst().get();
            String temp_str = temp_t.getElement();

            System.out.println(temp_t.getScore());
            redis.zremrangeByScore("z_article",temp_t.getScore()-0.001, temp_t.getScore()+0.001);
            System.out.println("删除最后一条数据");
        }
        return articleDao.add((int)article.getId(), article.getTitle(), article.getPubtime(),
                article.getLink(), article.getContent(),article.getCategory());
    }

    public int getTotalId(){
        return  articleDao.getTotalID();
    }

    public int getLastId(){
        return  articleDao.getLastId();
    }

    public List<CategoryStat> getCategoryStat()
    {
        return articleDao.getCategoryStat();
    }

    public Map<String,Object> getCategoryList() {
        List<CategoryStat> xlist = new ArrayList<CategoryStat>();
        Map<String,Object> xmap = new HashMap<String,Object>();
        xlist = articleDao.getCategoryStat();
        int[] xxdata = new int[xlist.size()];
        String[] xdata = new String[xlist.size()];
        int i = 0;
        for (CategoryStat stat: xlist){
            xxdata[i] = stat.getValue();
            xdata[i] = stat.getKey();
            xmap.put("xname",xdata);
            xmap.put("xcount",xxdata);
            i++;
        }
        return xmap;
    }
}
