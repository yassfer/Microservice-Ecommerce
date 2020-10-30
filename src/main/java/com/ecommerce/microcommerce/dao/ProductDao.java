package com.ecommerce.microcommerce.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.microcommerce.model.Product;

@Repository
public interface ProductDao extends JpaRepository<Product, Integer> {

	//public List<Product>findAll();
	Product findById(int id);
	//public Product save(Product produit);
	
	List<Product> findByPrixGreaterThan(int prixLimit);
	List<Product> findByNomLike(String recherche);
}
