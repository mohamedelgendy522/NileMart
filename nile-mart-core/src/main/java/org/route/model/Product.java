package org.route.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Product {
    private String name;
    @EqualsAndHashCode.Include
    private String sku;
    private BigDecimal price;
    private Category category;
    private boolean isActive;
    @Setter
    private int stockQuantity;
}
