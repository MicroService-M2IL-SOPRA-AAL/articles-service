package com.microservice.articlesservice.web.controller;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.microservice.articlesservice.dao.ArticleDao;
import com.microservice.articlesservice.model.Article;
import com.microservice.articlesservice.web.exceptions.ArticleIntrouvableException;
import com.microservice.articlesservice.web.exceptions.ArticlePrixEgalZeroException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@Api(value = "API pour les opérations CRUD sur les articles")
public class ArticleController {
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private HttpServletRequest requestContext;

    @ApiOperation(value = "Récupère la liste des articles !")
    @GetMapping(value = "/Articles")
    public MappingJacksonValue listeArticles() {
        logger.info("Début d'appel au service Articles pour la requête : " + requestContext.getHeader("req-id"));
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
        logger.info("Début d'appel au service Articles pour la requête : " + requestContext.getHeader("req-id"));
        return articleDao.findById(id).orElseThrow(() -> new ArticleIntrouvableException("L'article avec l'id " + id + " est INTROUVABLE"));
    }

    //ajouter un article
    @ApiOperation(value = "Permet de créer un article !")
    @PostMapping(value = "/Articles")
    public ResponseEntity<Void> ajouterArticle(@RequestBody Article article) {
        logger.info("Début d'appel au service Articles pour la requête : " + requestContext.getHeader("req-id"));
        if (article.getPrix() == 0)
            throw new ArticlePrixEgalZeroException("Impossible de créer cet article, son prix est égal à 0");
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
        logger.info("Début d'appel au service Articles pour la requête : " + requestContext.getHeader("req-id"));
        articleDao.deleteById(id);
    }

    @ApiOperation(value = "Permet de mettre à jour un article via son ID !")
    @PutMapping(value = "/Articles/{id}")
    public void updateArticle(@RequestBody Article article) {
        logger.info("Début d'appel au service Articles pour la requête : " + requestContext.getHeader("req-id"));
        articleDao.save(article);
    }

    @ApiOperation(value = "Retourne la marge de chaque produit")
    @GetMapping(value = "/Articles/AdminArticles")
    public Map<Article, Integer> calculerMargeArticle() {
        logger.info("Début d'appel au service Articles pour la requête : " + requestContext.getHeader("req-id"));
        return articleDao.findAll()
                .stream()
                .collect(Collectors.toMap(Function.identity(), article -> article.getPrix() - article.getPrixAchat()));
    }

    @ApiOperation(value = "Récupère la liste des articles triés par nom (asc)")
    @GetMapping(value = "/Articles/TriArticles")
    public MappingJacksonValue trierArticlesParOrdreAlphabetique() {
        logger.info("Début d'appel au service Articles pour la requête : " + requestContext.getHeader("req-id"));
        List<Article> articles = articleDao.findAllByOrderByNom();
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAll();
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("myDynamicFilter", monFiltre);
        MappingJacksonValue articlesFiltres = new MappingJacksonValue(articles);
        articlesFiltres.setFilters(listDeNosFiltres);
        return articlesFiltres;
    }
}
