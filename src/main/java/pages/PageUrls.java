package pages;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageUrls {

    public static final String BASE_URL = "https://www.saucedemo.com/";

    public static final String INVENTORY = "inventory";
    public static final String CART = "cart";
    public static final String CHECKOUT_STEP_ONE = "checkout-step-one";
    public static final String CHECKOUT_STEP_TWO = "checkout-step-two";
    public static final String CHECKOUT_COMPLETE = "checkout-complete";
}
