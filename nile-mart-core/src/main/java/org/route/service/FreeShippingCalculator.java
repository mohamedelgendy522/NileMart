package org.route.service;

import org.route.common.Money;
import org.route.model.Governorate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component(value = "freeShipping")
public class FreeShippingCalculator implements ShippingCalculator{
    @Override
    public BigDecimal calculate(Governorate g) {
        return Money.ZERO;
    }
}
