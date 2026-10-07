package com.foodcart;

import java.util.List;
import javax.ejb.Local;

/**
 * Business interface for the Online Food Cart.
 */
@Local
public interface FoodCart {

    /** Accept and store the customer's name. */
    void setCustomerName(String customerName);

    String getCustomerName();

    /** Add a food item (Pizza, Burger or Sandwich). Returns false if not on the menu. */
    boolean addFoodItem(String item);

    /** Remove one occurrence of a food item. Returns false if it was not in the cart. */
    boolean removeFoodItem(String item);

    /** View the current items in the cart. */
    List<String> viewCart();

    /** Remove everything from the cart. */
    void clearCart();

    /** Ends the session and releases the bean instance. */
    void checkout();
}
