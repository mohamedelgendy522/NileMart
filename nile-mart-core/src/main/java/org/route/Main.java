package org.route;

import org.route.common.Money;
import org.route.config.AppConfig;
import org.route.model.Category;
import org.route.model.Product;
import org.route.repository.InMemoryProductRepository;
import org.route.service.PricingService;
import org.route.service.ProductService;
import org.route.service.StandardPricingService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {
    void main(){

        //        var context = new ClassPathXmlApplicationContext("beans.xml");


//        StandardPricingService standard = context.getBean(StandardPricingService.class);
//        StandardPricingService standard2 = context.getBean(StandardPricingService.class);

//        ProductService p = context.getBean(ProductService.class);
//
//        System.out.println(p);

        var context = new AnnotationConfigApplicationContext(AppConfig.class);
//        var context2 = new AnnotationConfigApplicationContext(AppConfig.class);

//        StandardPricingService standard = context.getBean(StandardPricingService.class);
//        StandardPricingService standard2 = context2.getBean(StandardPricingService.class);

//        System.out.println(standard == standard2);
//        ProductService p = context.getBean(ProductService.class);
//        System.out.println(standard);

        ProductService productService = context.getBean(ProductService.class);

        System.out.println(productService);

        Product product = Product.builder()
                .price(BigDecimal.valueOf(100))
                .build();

        System.out.println(productService.getPrice("PH-SAM-S25", 2));

        PricingService pricing = new StandardPricingService();
        ProductService service = new ProductService(new InMemoryProductRepository(), pricing);
        System.out.println(service.getPrice("ACC-CHG-20W", 2));
        Map<Category, List<Product>> groupedProducts = productService.groupByCategory();
        for(Map.Entry<Category, List<Product>> entry : groupedProducts.entrySet()) {
            System.out.println(entry.getKey());
        }
        List<Product> products = service.search(p -> p.getStockQuantity() == 0);
        for(Product p : products) {
            System.out.println(p);
        }

        Product a = Product.builder()
                .sku("PH-X5-128")
                .name("Smartphone X5")
                .category(Category.ELECTRONICS)
                .price(Money.of("16000.00"))
                .stockQuantity(12)
                .build();

        Product b = Product.builder()
                .sku("PH-X5-128")
                .name("Different Name")
                .category(Category.BOOKS)
                .price(Money.of("500.00"))
                .stockQuantity(1)
                .build();

        Set<Product> set = new HashSet<>();
        set.add(a);
        set.add(b);

        System.out.println("a.equals(b): " + a.equals(b));
        System.out.println("HashSet size: " + set.size());
    }
}
