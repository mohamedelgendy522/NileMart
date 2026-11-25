package org.route.cart;

import lombok.RequiredArgsConstructor;
import org.route.common.Check;
import org.route.common.NotFoundException;
import org.route.model.Customer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class CartRegistry {

    private final ObjectProvider<ShoppingCart> cartProvider;
    private final ConcurrentHashMap<Integer, ShoppingCart> carts = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    public ShoppingCart open(Customer customer) {
        ShoppingCart cart = cartProvider.getObject();
        int number = counter.incrementAndGet();
        cart.open(number, customer);
        carts.put(number, cart);
        return cart;
    }

    public ShoppingCart get(int number) {
        return Check.found(Optional.ofNullable(carts.get(number)),
                () -> new NotFoundException("Cart " + number + " was not found.")
        );
    }

    public List<CartView> views() {
        return carts.values().stream()
                .map(ShoppingCart::view)
                .sorted(Comparator.comparingInt(CartView::number))
                .toList();
    }
}