package org.route.service;

import lombok.RequiredArgsConstructor;
import org.route.cart.ShoppingCart;
import org.route.common.OutOfStockException;
import org.route.common.StoreException;
import org.route.model.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final ProductService productService;
    private final ShippingQuoteService shippingQuoteService;
    private final TaxCalculator taxCalculator;
    private final OrderRegistry orderRegistry;
    private final List<NotificationSender> notificationSenders;

    public Quote quote(ShoppingCart cart , Governorate g){
        if (cart == null || g == null){
            throw new IllegalArgumentException("Cart and Governorate cannot be null");
        }
        if (cart.isEmpty()) {
            throw new StoreException("Cannot quote an empty cart.");
        }
        BigDecimal subtotal = cart.subTotal();
        BigDecimal vat = taxCalculator.calculate(subtotal);
        BigDecimal shippingFee = shippingQuoteService.quote(g, subtotal);
        BigDecimal total = subtotal.add(vat).add(shippingFee);

        return new Quote(subtotal, vat, shippingFee, total);
    }

    public synchronized Order placeOrder(ShoppingCart cart , Governorate g){
        if (cart == null || g == null){
            throw new IllegalArgumentException("Cart and Governorate cannot be null");
        }
        if (cart.isEmpty()) {
            throw new StoreException("Cannot quote an empty cart.");
        }



        for (Map.Entry<String,Integer> entry : cart.items().entrySet()) {
            int Qty = entry.getValue();
            int stockProduct = productService.getProductBySku(entry.getKey()).get().getStockQuantity();
            if (Qty > stockProduct) {
                throw new OutOfStockException("Product out of stock.");
            }
        }

        Quote q = quote(cart, g);

        List<OrderLine> orderLines = new ArrayList<>();
        for (Map.Entry<String,Integer> entry : cart.items().entrySet()) {
            productService.reduceStock(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String,Integer> entry : cart.items().entrySet()) {
            Optional<Product> product = productService.getProductBySku(entry.getKey());
            orderLines.add(new OrderLine(entry.getKey(),product.get().getName(),entry.getValue(),productService.getPrice(entry.getKey(), entry.getValue())));
        }

        Order order = new Order(orderRegistry.nextNumber(),cart.getCustomer(),g,orderLines,q.subtotal(),q.vat(),q.shippingFee(),q.total());
        orderRegistry.save(order);

        for (NotificationSender sender : notificationSenders) {
            sender.send(cart.getCustomer(),"Order placed Order placed successfully");
        }

        cart.clear();

        return order;
    }
}
