package com.treebee.blog.dao;

import com.treebee.blog.entity.Article;
import com.treebee.blog.entity.CategoryStat;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Mapper
@Repository
public interface ArticleDao {


    @Select("select * from news where id = #{id}")
    Article findArticleById(int id);

    @Select("select id,title,pubtime from news order by id DESC limit #{start},#{limit}")
    List<Article> findUsers(Map param);

    @Select("select count(*) from news")
    int getTotalUser(Map param);

    @Select("select count(*) from news")
    int getTotalID();

    @Select("select content from news where id = #{id}")
    String findHtmlNews(@Param("id") int id);

    @Insert("insert into news values (#{id},#{title},#{pubtime},#{url},#{content},#{category})")
    int add(
            @Param("id") int id,
            @Param("title") String title,
            @Param("pubtime") String pubtime,
            @Param("url") String url,
            @Param("content") String content,
            @Param("category") String category);

    int deleteBatch(Integer[] ids);

    int editBatch(@Param("title") String title,@Param("id") Long ids);

    List<CategoryStat> getCategoryStat();

    @Select("select id from news order by pubtime DESC limit 9,1")
    int getCacheOldestId();

    @Select("select id from news order by id DESC limit 1")
    int getLastId();
}
