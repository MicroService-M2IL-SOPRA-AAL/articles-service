package com.microservice.articlesservice.dao;

import com.microservice.articlesservice.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleDao extends JpaRepository<Article, Integer> {

    List<Article> findAllByOrderByNom();

}
