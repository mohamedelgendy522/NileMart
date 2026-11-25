package org.route.repository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.route.common.StoreException;
import org.route.model.Category;
import org.route.model.Product;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final Map<String, Product> store = new ConcurrentHashMap<>();

    @PostConstruct
    public void loadCatalog() {
        InputStream inputStream = getClass().getResourceAsStream("/products.csv");
        if (inputStream == null) {
            throw new StoreException("The catalog could not be loaded.");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",");
                String sku = parts[0].trim();
                String name = parts[1].trim();
                Category category = Category.valueOf(parts[2].trim());
                BigDecimal price = new BigDecimal(parts[3].trim());
                int stock = Integer.parseInt(parts[4].trim());

                Product product = new Product(name, sku, price,category, true, stock);
                save(product);
            }
        } catch (IOException e) {
            throw new StoreException("The catalog could not be loaded.", e);
        }

        System.out.println("Catalog loaded: " + store.size() + " products.");
    }

    @PreDestroy
    public void closeCatalog() {
        System.out.println("Catalog closed.");
    }

    @Override
    public List<Product> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Product save(Product entity) {
        store.put(entity.getSku(), entity);
        return entity;
    }
}