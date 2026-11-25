package org.route;

import org.route.cart.CartRegistry;
import org.route.config.AppConfig;
import org.route.console.StoreConsole;
import org.route.service.CheckoutService;
import org.route.service.ProductService;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class Main {

    public static void main(String[] args) {
        var context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);
        try (context; WebServer server = new WebServer(context, 8081)) {
            server.start();
            new StoreConsole(
                    context.getBean(CartRegistry.class),
                    context.getBean(ProductService.class),
                    context.getBean(CheckoutService.class)
            ).run();
        }
    }
}