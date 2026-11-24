package org.route.model;

public abstract class AbstractNotificationSender implements NotificationSender{
    @Override
    public final void send(Customer customer, String message) {
        if(supports(customer)){
            System.out.println("[" + channel() + "] to " + address(customer) + ": " + message);
        }
    }
    public abstract String channel();
    public abstract String address(Customer customer);
}
