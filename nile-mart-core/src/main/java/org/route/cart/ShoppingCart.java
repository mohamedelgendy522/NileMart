package org.route.cart;

import lombok.Getter;
import org.route.common.Money;
import org.route.common.OutOfStockException;
import org.route.common.StoreException;
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
        if(customer!=null){
            throw new IllegalStateException("IT Already has customer");
        }
        this.cardNumber = number;
        this.customer = customer;
    }
    public synchronized void addItem(String Sku , int qty){
        if(qty <= 0){
            throw new StoreException("Quantity should be more than 0");
        }
        Optional<Product> product = productService.getProductBySku(Sku);
        if(product.isEmpty()){
            throw new StoreException("Product not found");
        }
        else{
            if(product.get().getStockQuantity() != 0){
                int newQty = items.getOrDefault(Sku,0) + qty;
                if(newQty > maxQuantityPerProduct){
                    throw new StoreException("You can add at most " + maxQuantityPerProduct + " of " + product.get().getName());
                }
                if(newQty > product.get().getStockQuantity() ){
                    throw new OutOfStockException("Only " + product.get().getStockQuantity() + " available for " + product.get().getName());
                }
                items.put(Sku, newQty);
            }
            else{
                throw new OutOfStockException(product.get().getName() + " is out of Stock");
            }
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
}
