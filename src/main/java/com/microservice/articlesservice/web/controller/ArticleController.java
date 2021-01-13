package com.microservice.articlesservice.web.controller;

import com.microservice.articlesservice.dao.ArticleDao;
import com.microservice.articlesservice.model.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ArticleController {
    @Autowired
    private ArticleDao articleDao;

    @GetMapping(value = "/Articles")
    public List<Article> listeArticles() {
        return articleDao.findAll();
    }

    //Récupérer un article par son Id
    @GetMapping(value = "/Articles/{id}")
    public Article afficherUnArticle(@PathVariable int id) {
        return articleDao.findById(id);
    }

    // Ajouter un article
    @PostMapping(value = "/Articles")
    public void ajouterArticle(@RequestBody Article article) {
        articleDao.save(article);
    }
}
