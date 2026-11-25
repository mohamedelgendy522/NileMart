package org.route.controller;

import lombok.RequiredArgsConstructor;
import org.route.cart.CartRegistry;
import org.route.cart.ShoppingCart;
import org.route.common.StoreException;
import org.route.model.Governorate;
import org.route.model.Order;
import org.route.service.CheckoutService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CheckoutController {
    private final CheckoutService checkoutService;
    private final CartRegistry cartRegistry;

    @GetMapping("/{number}/checkout")
    public String form(@PathVariable int number, @RequestParam(required = false) Governorate governorate, Model model) {
        ShoppingCart cart = cartRegistry.get(number);
        model.addAttribute("cart", cart.view());
        model.addAttribute("governorates", Governorate.values());
        model.addAttribute("selected", governorate);
        model.addAttribute("quote", governorate == null ? null : checkoutService.quote(cart, governorate));
        return "checkout/form";
    }

    @PostMapping("/{number}/checkout")
    public String place(@PathVariable int number, @RequestParam(required = false) Governorate governorate, RedirectAttributes redirectAttributes) {
        if (governorate == null) {
            throw new StoreException("Choose a governorate.");
        }
        ShoppingCart cart = cartRegistry.get(number);
        Order order = checkoutService.placeOrder(cart, governorate);

        redirectAttributes.addFlashAttribute("message", "Order " + order.orderNumber() + " confirmed.");
        return "redirect:/orders/" + order.orderNumber();
    }
}