package org.route.config;


import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.time.Clock;


@Configuration
@ComponentScan("org.route")
//@ImportResource("/beans.xml")
@PropertySource("/application.properties")
@EnableAspectJAutoProxy
public class AppConfig {

    @Bean
    PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public java.time.Clock clock() {
        return Clock.systemDefaultZone();
    }

//    @Bean
//    @Scope("prototype")
//    ProductService productService(){
//        return new ProductService(inMemoryProductRepository(), standardPricingService());
//    }
//
//    @Bean
//    InMemoryProductRepository inMemoryProductRepository() {
//        return new InMemoryProductRepository();
//    }
//
//    @Bean
//    StandardPricingService standardPricingService() {
//        return new StandardPricingService();
//    }
//
//    @Bean
//    PromotionalPricingService promotionalPricingService() {
//        return new PromotionalPricingService(BigDecimal.valueOf(10));
//    }

}
