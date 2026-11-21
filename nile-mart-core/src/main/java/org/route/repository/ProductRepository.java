package org.route.repository;

import org.route.model.Product;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductRepository extends Repository<Product, String> {
    default Optional<Product> findBySku(String sku) {
        return findById(sku);
    }
}
