package org.route.cart;

import lombok.Getter;
import org.route.common.*;
import org.route.model.Customer;
import org.route.model.Product;
import org.route.service.ProductService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.*;


@Component
@Scope("prototype")
public class ShoppingCart {
    @Getter
    private int cardNumber;
    @Getter
    private Customer customer;
    private LinkedHashMap<String, Integer> items;
    private final ProductService productService;
    private final int maxQuantityPerProduct;
    public ShoppingCart(ProductService productService ,@Value("${store.max-quantity-per-product}") int maxQuantityPerProduct) {
        this.productService = productService;
        this.maxQuantityPerProduct = maxQuantityPerProduct;
        this.items = new LinkedHashMap<>();
    }
    public void open(int number , Customer customer) {
        if(this.customer != null){
            throw new IllegalStateException("IT Already has customer");
        }
        this.cardNumber = number;
        this.customer = customer;
    }
    public synchronized void addItem(String Sku , int qty){
        if(qty <= 0){
            throw new StoreException("Quantity should be more than 0");
        }
        Product product = Check.found(productService.getProductBySku(Sku),
                () -> new NotFoundException("Product " + Sku + " was not found."));
        if(product.getStockQuantity() != 0){
                int newQty = items.getOrDefault(Sku,0) + qty;
                if(newQty > maxQuantityPerProduct){
                    throw new StoreException("You can add at most " + maxQuantityPerProduct + " of " + product.getName());
                }
                if(newQty > product.getStockQuantity() ){
                    throw new OutOfStockException("Only " + product.getStockQuantity() + " available for " + product.getName());
                }
                items.put(Sku, newQty);
            }
            else{
                throw new OutOfStockException(product.getName() + " is out of Stock");
            }
    }

    public BigDecimal subTotal(){
        return items.entrySet().stream().map(entry -> {
            String sku = entry.getKey();
            int quantity = entry.getValue();
            return productService.getPrice(sku,quantity);
        }).reduce(Money.ZERO, BigDecimal::add);
    }
    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
    }

    public Map<String, Integer> items() {
        return Collections.unmodifiableMap(items);
    }
    public CartView view(){
        List<LineView> lines = new ArrayList<>();
        int itemCount = 0;
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String sku = entry.getKey();
            int quantity = entry.getValue();
            Product product = productService.getProductBySku(sku).orElseThrow(() -> new StoreException("Product not found"));
            itemCount+=quantity;
            LineView line = new LineView(sku, product.getName(), quantity, productService.getPrice(sku, quantity));
            lines.add(line);
        }
        return new CartView(cardNumber, customer, lines, itemCount, subTotal(), isEmpty());
    }

    public int getCardNumber() {
        return cardNumber;
    }


    public Customer getCustomer() {
        return customer;
    }

}
