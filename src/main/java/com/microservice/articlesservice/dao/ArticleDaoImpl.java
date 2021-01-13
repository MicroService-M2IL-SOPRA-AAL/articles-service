package com.microservice.articlesservice.dao;

import com.microservice.articlesservice.model.Article;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ArticleDaoImpl implements ArticleDao {
    public static List<Article> articles = new ArrayList<>();

    static {
        articles.add(new Article(1, "Ordinateur portable", 350, 250));
        articles.add(new Article(2, "Aspirateur Robot", 500, 200));
        articles.add(new Article(3, "Table de Ping Pong", 750, 500));
    }

    @Override
    public List<Article> findAll() {
        return articles;
    }

    @Override
    public Article findById(int id) {
        return articles.stream().filter(article -> article.getId() == id).findFirst().orElse(null);
    }

    @Override
    public Article save(Article article) {
        articles.add(article);
        return article;
    }
}
