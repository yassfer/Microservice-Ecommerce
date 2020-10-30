package com.ecommerce.microcommerce.web.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.exception.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class ProductController {
	@Autowired
	private ProductDao productDao;
	
	@RequestMapping(value="/Produits", method=RequestMethod.GET)
	public MappingJacksonValue listProduits(){
		Iterable <Product> produits = productDao.findAll();
		
		SimpleBeanPropertyFilter monFiltre =
				SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
		
		FilterProvider listDeNosFiltre = new
				SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
		
		MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);
		
		produitsFiltres.setFilters(listDeNosFiltre);
		
		return produitsFiltres;
	}
	
	@GetMapping(value ="Produits/{id}")
	public Product afficherUnProduit(@PathVariable int id){
		Product produit= productDao.findById(id);
		if(produit==null) throw new ProduitIntrouvableException("Le produit avec l'id "+id+
				" est INTROUVABLE. Ecran Bleu si je pouvais.");
		
		return produit;
	}
	
	@GetMapping(value ="prix/produits/{prixLimit}")
	public List<Product> testPrix(@PathVariable int prixLimit){
		return productDao.findByPrixGreaterThan(prixLimit);
	}
	
	@GetMapping (value ="nom/produits/{recherche}")
	public List<Product> testNom(@PathVariable String recherche){
		return productDao.findByNomLike("%"+recherche+"%");
	}
	
	@PostMapping(value="/Produits")
	public ResponseEntity <Void> ajouterProduit(@Valid @RequestBody Product product){
		
		Product productAdded = productDao.save(product);
		
		if(productAdded==null){
			return ResponseEntity.noContent().build();
		}
		
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("{id}")
				.buildAndExpand(productAdded.getId())
				.toUri();
		return ResponseEntity.created(location).build();
	}
	
	@DeleteMapping(value="/Produits/{id}")
	public void supprimerProduit(@PathVariable int id){
		productDao.deleteById(id);
	}
	
	@PutMapping(value="/Produits")
	public void updateProduit(@RequestBody Product product){
		productDao.save(product);
	}
}
