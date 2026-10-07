
package com.foodcart;

import java.util.List;
import javax.ejb.Local;

/**
 * Local business interface for the Online Food Cart.
 *
 * The interface defines the operations that can be
 * performed on the customer's cart.
 */
@Local
public interface FoodCart {

    // Customer information
    void setCustomerName(String customerName);

    String getCustomerName();


    // Cart operations
    boolean addFoodItem(String item);

    boolean removeFoodItem(String item);

    List<String> viewCart();

    void clearCart();


    // Price and order information
    double getTotalAmount();

    int getCartItemCount();


    // Ends the Stateful Session Bean
    void checkout();
}

