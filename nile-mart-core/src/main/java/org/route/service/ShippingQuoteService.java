package org.route.service;

import org.route.model.Governorate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ShippingQuoteService {

    private final ShippingCalculator primaryCalculator;
    private final ShippingCalculator freeShippingCalculator;
    private final BigDecimal freeShippingThreshold;

    public ShippingQuoteService(
            ShippingCalculator primaryCalculator,
            @Qualifier("freeShipping") ShippingCalculator freeShippingCalculator,
            @Value("${store.free-shipping-threshold}") BigDecimal freeShippingThreshold) {
        this.primaryCalculator = primaryCalculator;
        this.freeShippingCalculator = freeShippingCalculator;
        this.freeShippingThreshold = freeShippingThreshold;
    }
    public BigDecimal quote(Governorate g, BigDecimal subtotal){
        if(g.getZone().isFreeShippingEligible()){
            if(subtotal.compareTo(freeShippingThreshold) >= 0){
                return freeShippingCalculator.calculate(g);
            }
        }
        return primaryCalculator.calculate(g);
    }
}