package org.route.model;

import java.math.BigDecimal;

public record Quote(BigDecimal subtotal,
                    BigDecimal vat, BigDecimal shippingFee, BigDecimal total) {
}
