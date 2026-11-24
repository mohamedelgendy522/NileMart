package org.route;

import org.route.cart.CartView;
import org.route.cart.LineView;
import org.route.cart.ShoppingCart;
import org.route.config.AppConfig;
import org.route.common.StoreException;
import org.route.model.Customer;
import org.route.model.Governorate;
import org.route.common.Money;

import org.route.model.Order;
import org.route.model.Product;
import org.route.service.CheckoutService;
import org.route.service.ProductService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class);
             var scanner = new Scanner(System.in)) {

            ProductService productService = context.getBean(ProductService.class);
            CheckoutService checkoutService = context.getBean(CheckoutService.class);
            Map<Integer, ShoppingCart> carts = new HashMap<>();

            boolean running = true;
            while (running) {
                System.out.println("========== NILEMART ==========");
                System.out.println("1. Show products");
                System.out.println("2. Create a cart");
                System.out.println("3. Add a product to a cart");
                System.out.println("4. Show a cart");
                System.out.println("5. Check out a cart");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");

                String choice = scanner.nextLine().trim();

                try {
                    switch (choice) {
                        case "1" -> {
                            for (Product p : productService.getAllProducts()) {
                                System.out.println(p.getSku() + " - " + p.getName()
                                        + " | " + Money.format(p.getPrice())
                                        + " | Stock: " + p.getStockQuantity());
                            }
                        }
                        case "2" -> {
                            System.out.print("Customer name: ");
                            String name = scanner.nextLine();
                            System.out.print("Customer email: ");
                            String email = scanner.nextLine();
                            System.out.print("Customer phone: ");
                            String phone = scanner.nextLine();

                            ShoppingCart cart = context.getBean(ShoppingCart.class);
                            cart.setCustomer(new Customer(name, email, phone));
                            carts.put(cart.getCardNumber(), cart);
                            System.out.println("Cart created: #" + cart.getCardNumber());
                        }
                        case "3" -> {
                            System.out.print("Cart number: ");
                            int cartNumber = Integer.parseInt(scanner.nextLine().trim());
                            ShoppingCart cart = carts.get(cartNumber);
                            if (cart == null) {
                                throw new StoreException("Cart not found: " + cartNumber);
                            }
                            System.out.print("Product SKU: ");
                            String sku = scanner.nextLine().trim();
                            System.out.print("Quantity: ");
                            int qty = Integer.parseInt(scanner.nextLine().trim());

                            cart.addItem(sku, qty);
                            System.out.println("Added to cart.");
                        }
                        case "4" -> {
                            System.out.print("Cart number: ");
                            int cartNumber = Integer.parseInt(scanner.nextLine().trim());
                            ShoppingCart cart = carts.get(cartNumber);
                            if (cart == null) {
                                throw new StoreException("Cart not found: " + cartNumber);
                            }
                            CartView view = cart.view();
                            System.out.println("Cart #" + view.number() + " | Customer: " + view.customer());
                            for (LineView line : view.lines()) {
                                System.out.println("  " + line.sku() + " - " + line.name()
                                        + " x" + line.quantity() + " = " + Money.format(line.linePrice()));
                            }
                            System.out.println("Subtotal: " + Money.format(view.subtotal()));
                        }
                        case "5" -> {
                            System.out.print("Cart number: ");
                            int cartNumber = Integer.parseInt(scanner.nextLine().trim());
                            ShoppingCart cart = carts.get(cartNumber);
                            if (cart == null) {
                                throw new StoreException("Cart not found: " + cartNumber);
                            }
                            System.out.print("Governorate: ");
                            Governorate g = Governorate.valueOf(scanner.nextLine().trim().toUpperCase());

                            Order order = checkoutService.placeOrder(cart, g);
                            System.out.println("Order placed: " + order.orderNumber());
                            System.out.println("Subtotal: " + Money.format(order.subtotal()));
                            System.out.println("VAT: " + Money.format(order.vat()));
                            System.out.println("Shipping: " + Money.format(order.shippingFee()));
                            System.out.println("Total: " + Money.format(order.total()));
                        }
                        case "0" -> running = false;
                        default -> System.out.println("Invalid option.");
                    }
                } catch (StoreException e) {
                    System.out.println("Error: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid input: " + e.getMessage());
                }
            }
        }
    }
}