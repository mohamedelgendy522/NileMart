# NileMart Answers

## Checkpoint 1

1. `ManualWiring` is the class that has `new StandardPricingService()`. To switch to promotional pricing, I have to edit `ManualWiring` and pass `new PromotionalPricingService(...)` instead.
2. No, I didn't need to touch `ProductService`. It only depends on the `PricingService` interface, so it doesn't care which implementation it gets.
3. The main wiring class ends up owning all the `new` calls, and with 60 classes it becomes really messy and hard to maintain. That's what Spring solves by creating and wiring them for us.
4. `HashMap` uses `hashCode()` (which depends on `sku`) to decide which bucket to store the key in. If you change the `sku` later, its hash code changes, so the map looks in the wrong bucket and won't find the product anymore.

## Checkpoint 2

1. The classes with Spring annotations (`@Configuration`, `@Service`, `@Repository`, `@Component`) and `@Bean` methods appear in the list, like `appConfig`, `inMemoryProductRepository`, `productService`, `standardPricingService`, and `promotionalPricingService`. Normal data classes like `Product`, `Category`, and exception classes don't appear because they are not Spring beans.
2. Spring throws `NoUniqueBeanDefinitionException` and mentions two candidates: `standardPricingService` and `promotionalPricingService`.
3. No. When `@ComponentScan` finds `ProductService`, it only registers its bean definition first. The actual object is created later when the container builds the beans.

## Checkpoint 3

1. The first `ShippingCalculator` parameter has no qualifier, so Spring injects `ZoneShippingCalculator` because it has `@Primary`. The second parameter has `@Qualifier("freeShipping")`, so Spring injects `FreeShippingCalculator` by its bean name.
2. As long as `ZoneShippingCalculator` still has `@Primary`, nothing breaks because Spring picks the primary one for the default parameter. If there was no `@Primary` (or more than one), Spring would fail with `NoUniqueBeanDefinitionException`.
3. `System.out.println(shippingQuoteService.quote(Governorate.CAIRO, Money.of("5000.00")) + " / " + shippingQuoteService.quote(Governorate.MATROUH, Money.of("999999.00")));` (prints `0.00 / 120.00`).
4. None. I just add the new `@Component` class extending `AbstractNotificationSender`, and Spring automatically adds it to `List<NotificationSender>`.

## Checkpoint 4

1. `ProductService` is a singleton by default, so both `getBean` calls return the exact same object (`true`). `ShoppingCart` has `@Scope("prototype")`, so every `getBean` call creates a new object (`false`).
2. It prints only once at startup, not on every `getBean`, because `InMemoryProductRepository` is a singleton so Spring creates and initializes it once.
3. Spring only builds a singleton one time, so if it takes `ShoppingCart` in its constructor, it gets one cart forever and all customers would share it. To avoid this, we ask the container for a fresh cart each time (using `ObjectProvider<ShoppingCart>` in `CartRegistry`).
4. If the SKU is not in the map yet, `get(sku)` returns `null`, and doing `null + qty` causes a `NullPointerException` during unboxing. `getOrDefault(sku, 0)` safely returns `0`.
5. `LinkedHashMap` keeps the items in the order the customer added them, while `HashMap` has no order.

## Checkpoint 6

1. The five controllers (`productController`, `cartController`, `checkoutController`, `orderController`, `globalExceptionHandler`), `cartRegistry`, and the three `WebConfig` beans (`templateResolver`, `templateEngine`, `thymeleafViewResolver`) all appear in the list. `StoreConsole` is not in the list because it's not a bean — it's just a plain Java object created with `new` in `Main`.
2. `CartRegistry` asks the container for a new cart inside `open(Customer customer)` on this line: `ShoppingCart cart = cartProvider.getObject();`.
3. Both the console and the browser use the same Spring container, so they share the same singleton `CartRegistry`. If `StoreConsole` created its own `new CartRegistry()`, it would have a separate map in memory and the browser wouldn't see console carts.
4. `POST /carts` redirects to follow the Post/Redirect/Get pattern. With a redirect, pressing F5 just refreshes the GET page safely; if it returned `carts/view` directly, F5 would submit the POST form again and create a duplicate cart.
5. The `synchronized` keyword on `CheckoutService.placeOrder` makes one request run first while the other waits. The first tab finishes the order and clears the cart, and the second tab gets a 400 error page saying you can't check out an empty cart.
6. The browser shows Tomcat's 404 Not Found page, because without `@Controller` Spring doesn't map any `/carts` routes to `CartController`.

## Principles

- **Encapsulation**: `Product` and `ShoppingCart` keep their fields private, and the cart only allows opening once and returns an unmodifiable map of its items.
- **Abstraction, interfaces**: Services and callers talk to interfaces like `ProductRepository`, `PricingService`, `ShippingCalculator`, and `NotificationSender` instead of concrete classes.
- **Polymorphism**: `CheckoutService` loops over `List<NotificationSender>` and `GlobalExceptionHandler` calls `ex.status()` without any `instanceof` or type checks.
- **Inheritance**: `AbstractNotificationSender` holds the shared `final send()` template method for SMS and Email senders, and `NotFoundException` and `OutOfStockException` extend `StoreException`.
- **Composition over inheritance**: `CheckoutService` extends nothing and does its job by combining five injected collaborators.
- **SOLID**: `TaxCalculator` only handles VAT (S), adding a new `NotificationSender` needs no edits to existing classes (O), `ZoneShippingCalculator` and `FreeShippingCalculator` are interchangeable (L), interfaces like `NotificationSender` stay small (I), and beans like `ProductService` and `CheckoutService` inject interfaces through constructors (D).
- **Generics**: `Repository<T, ID>` works for any entity/ID type, and `Check.found` is a generic helper to unwrap `Optional` or throw a `StoreException`.
- **Collections**: We picked `ConcurrentHashMap` in `InMemoryProductRepository` and `CartRegistry` for thread safety, `LinkedHashMap` in `ShoppingCart` for insertion order, `EnumMap` in `ProductService` for enum order, `EnumSet` in `Zone`, and `CopyOnWriteArrayList` in `OrderRegistry`.
- **Streams and lambdas**: Used in `ShoppingCart` (subtotal), `ProductService` (`groupByCategory`, `search`), and `OrderRegistry` (`revenue`) instead of manual loops.
- **Optional**: `ProductRepository.findBySku` and `OrderRegistry.findByNumber` return `Optional` instead of `null`, and we unwrap them with `Check.found` or `orElseThrow`.
- **Exceptions**: Business errors use unchecked `StoreException` subclasses, and `InMemoryProductRepository` catches the checked `IOException` while reading CSV and wraps it in a `StoreException` with the cause.
- **Immutability, records**: `Customer`, `Order`, `OrderLine`, `Quote`, `CartView`, and `LineView` are Java records with validation and `List.copyOf` in compact constructors.
- **Enums**: `Category`, `Zone`, and `Governorate` are enums with fields, constructors, and helper methods instead of plain strings.
- **equals, hashCode, toString**: `Product` uses Lombok `@ToString` and `@EqualsAndHashCode` on `sku` only.
- **Numbers and BigDecimal**: `Money`, `TaxCalculator`, and `ShippingQuoteService` use `BigDecimal` (scale 2, `HALF_UP`) and compare values using `compareTo`.
- **static, final, nested**: `Money` and `Check` are `final` utility classes with private constructors and `static` members, and bean fields are `final`.
- **Default methods**: `ProductRepository.findBySku` and `NotificationSender.supports` use interface default methods to share behavior.
- **Concurrency**: `CartRegistry` and `OrderRegistry` use `AtomicInteger` and concurrent collections, and `CheckoutService.placeOrder` and `ShoppingCart.addItem` are `synchronized`.
- **I/O, try-with-resources**: `InMemoryProductRepository` closes its `BufferedReader` and `Main` closes `WebServer` and the Spring context using try-with-resources.
- **Date and time**: `OrderRegistry` uses an injected `Clock` with `LocalDate.now(clock)` and `DateTimeFormatter.BASIC_ISO_DATE`.
- **Strings and formatting**: `OrderRegistry` formats order numbers with `String.format` (`NM-%s-%05d`) and `Money.format` formats prices with `Locale.US` (`#,##0.00`).
