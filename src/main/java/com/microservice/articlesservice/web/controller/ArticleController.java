package com.microservice.articlesservice.web.controller;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.microservice.articlesservice.dao.ArticleDao;
import com.microservice.articlesservice.model.Article;
import com.microservice.articlesservice.web.exceptions.ArticleIntrouvableException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@Api(value = "API pour les opérations CRUD sur les articles")
public class ArticleController {
    @Autowired
    private ArticleDao articleDao;

    @ApiOperation(value = "Récupère la liste des articles !")
    @GetMapping(value = "/Articles")
    public MappingJacksonValue listeArticles() {
        List<Article> articles = articleDao.findAll();
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("myDynamicFilter", monFiltre);
        MappingJacksonValue articlesFiltres = new MappingJacksonValue(articles);
        articlesFiltres.setFilters(listDeNosFiltres);
        return articlesFiltres;
    }

    //Récupérer un article par son Id
    @ApiOperation(value = "Récupère un article grâce à son ID à condition que celui-ci soit en stock !")
    @GetMapping(value = "/Articles/{id}")
    public Article afficherUnArticle(@PathVariable int id) {
        return articleDao.findById(id).orElseThrow(() -> new ArticleIntrouvableException("L'article avec l'id " + id + " est INTROUVABLE"));
    }

    //ajouter un article
    @ApiOperation(value = "Permet de créer un article !")
    @PostMapping(value = "/Articles")
    public ResponseEntity<Void> ajouterArticle(@RequestBody Article article) {
        Article articleAdded = articleDao.save(article);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(articleAdded.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @ApiOperation(value = "Permet de supprimer un article via son ID !")
    @DeleteMapping(value = "/Articles/{id}")
    public void supprimerArticle(@PathVariable int id) {
        articleDao.deleteById(id);
    }

    @ApiOperation(value = "Permet de mettre à jour un article via son ID !")
    @PutMapping(value = "/Articles/{id}")
    public void updateArticle(@RequestBody Article article) {
        articleDao.save(article);
    }
}
