package org.route.controller;


import lombok.RequiredArgsConstructor;
import org.route.cart.CartRegistry;
import org.route.common.Check;
import org.route.common.NotFoundException;
import org.route.model.OrderRegistry;
import org.route.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final ProductService productService;
    private final OrderRegistry orderRegistry;

    @GetMapping
    public String getOrders(Model model) {
        model.addAttribute("orders", orderRegistry.findAll());
        model.addAttribute("revenue",orderRegistry.revenue());
        return "orders/list";
    }

    @GetMapping("/{orderNumber}")
    public String view(@PathVariable String orderNumber, Model model){
        model.addAttribute("order", Check.found(orderRegistry.
                findByNumber(orderNumber) , () -> new NotFoundException("Order " + orderNumber + " was not found.")));
        return "orders/confirmation";
    }

}
