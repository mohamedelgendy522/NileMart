package org.route.model;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class SmsNotificationSender extends AbstractNotificationSender{

    @Override
    public boolean supports(Customer customer) {
        return customer != null
                && customer.phone() != null
                && !customer.phone().isBlank();
    }

    @Override
    public String channel() {
        return "SMS";
    }

    @Override
    public String address(Customer customer) {
        return customer.phone();
    }
}
