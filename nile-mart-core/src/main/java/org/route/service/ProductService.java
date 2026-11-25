package org.route.service;

import lombok.RequiredArgsConstructor;
import org.route.common.NotFoundException;
import org.route.common.OutOfStockException;
import org.route.model.Category;
import org.route.model.Product;
import org.route.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final PricingService pricingService;



    public BigDecimal getPrice(String sku, int qty) {
        Product product = repository.findBySku(sku)
                .filter(Product::isActive)
                .orElseThrow(() -> new NotFoundException("Product isn't available."));
        return pricingService.getPrice(product, qty);
    }

    public List<Product> getAllProducts() {
        Comparator<Product> comparator = Comparator.comparing(Product::getCategory).thenComparing(Product::getName);

        List<Product> products = new ArrayList<>(repository.findAll());

        Collections.sort(products, comparator);

        return products;
    }

    public Optional<Product> getProductBySku(String sku) {
        Optional<Product> product = repository.findBySku(sku);

        if (product.isEmpty()) {
            throw new NotFoundException("Product not found");
        }

        return product;
    }
    public void reduceStock(String sku, int qty) {
        if(qty <= 0){
             throw new IllegalArgumentException("Quantity must be positive");
        }
        Product product = repository.findBySku(sku)
                .filter(Product::isActive)
                .orElseThrow(() -> new NotFoundException("Product isn't available."));
        if(product.getStockQuantity() - qty < 0){
            throw new OutOfStockException("Product out of stock");
        }
        product.setStockQuantity(product.getStockQuantity() - qty);
        repository.save(product);
    }
    public List<Product> search(Predicate<Product> filter) {
        return repository.findAll().stream().filter(filter).collect(Collectors.toList());
    }
    public Map<Category, List<Product>> groupByCategory() {
        return repository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Product::getCategory, () -> new EnumMap<>(Category.class),
                Collectors.toList()));
    }
}
