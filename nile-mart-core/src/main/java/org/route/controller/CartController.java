package org.route.controller;

import lombok.RequiredArgsConstructor;
import org.route.cart.CartRegistry;
import org.route.cart.ShoppingCart;
import org.route.common.StoreException;
import org.route.controller.form.AddItemForm;
import org.route.controller.form.NewCartForm;
import org.route.model.Customer;
import org.route.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {

    private final ProductService productService;
    private final CartRegistry cartRegistry;

    @GetMapping
    public String list(Model model){
        model.addAttribute("carts", cartRegistry.views());
        return "carts/list";
    }
    @GetMapping("/new")
    public String newForm(Model model){
        model.addAttribute("form", new NewCartForm());
        return "carts/new";
    }

    @GetMapping("/{number}")
    public String view(@PathVariable int number, Model model){
        model.addAttribute("cart",cartRegistry.get(number).view());
        model.addAttribute("products",productService.search(x -> x.getStockQuantity() > 0));
        return "carts/view";
    }

    @PostMapping()
    public String create(@ModelAttribute("form") NewCartForm form, RedirectAttributes redirectAttributes) {
        String email = (form.getEmail() == null || form.getEmail().isBlank()) ? null : form.getEmail();
        Customer customer = new Customer(form.getFullName(), form.getPhone(), email);
        ShoppingCart cart = cartRegistry.open(customer);

        redirectAttributes.addFlashAttribute("message", "Cart " + cart.getCardNumber() + " created for " + customer.fullName() + ".");
        return "redirect:/carts/" + cart.getCardNumber();
    }

    @PostMapping("/items")
    public String addItem(@ModelAttribute AddItemForm form, RedirectAttributes redirectAttributes) {
        if (form.getCartNumber() == null) {
            throw new StoreException("Choose a cart.");
        }
        if (form.getSku() == null || form.getSku().isBlank()) {
            throw new StoreException("Choose a product.");
        }
        if (form.getQuantity() == null) {
            throw new StoreException("Enter a quantity.");
        }

        cartRegistry.get(form.getCartNumber()).addItem(form.getSku(), form.getQuantity());
        String productName = productService.getProductBySku(form.getSku()).get().getName();

        redirectAttributes.addFlashAttribute(
                "message",
                "Added " + form.getQuantity() + " x " + productName + " to cart " + form.getCartNumber() + "."
        );
        return "redirect:/carts/" + form.getCartNumber();
    }
}
