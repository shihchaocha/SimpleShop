package com.example.simpleshop.repo;

import com.example.simpleshop.models.LineItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineItemRepository extends JpaRepository<LineItem, Integer> {
}
