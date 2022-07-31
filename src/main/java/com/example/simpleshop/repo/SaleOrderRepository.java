package com.example.simpleshop.repo;

import com.example.simpleshop.models.SaleOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleOrderRepository extends JpaRepository<SaleOrder,Integer> {
}
