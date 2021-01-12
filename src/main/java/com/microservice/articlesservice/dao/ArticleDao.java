package com.microservice.articlesservice.dao;

import com.microservice.articlesservice.model.Article;

import java.util.List;

public interface ArticleDao {
    List<Article> findAll();

    Article findById(int id);

    Article save(Article article);
}
