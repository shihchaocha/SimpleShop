package com.example.simpleshop.repo;

import com.example.simpleshop.models.ProductTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface ProductTagRepository extends JpaRepository<ProductTag, Integer> {
}
