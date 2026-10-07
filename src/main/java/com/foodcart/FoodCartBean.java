package com.foodcart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ejb.Remove;
import javax.ejb.Stateful;

/**
 * Stateful Session Bean: one instance per client, so the customer name
 * and cart contents are remembered between method calls.
 */
@Stateful
public class FoodCartBean implements FoodCart {

    private static final List<String> MENU = Arrays.asList("Pizza", "Burger", "Sandwich");

    // Conversational state - kept for the life of this bean instance
    private String customerName;
    private final List<String> cart = new ArrayList<>();

    @Override
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public String getCustomerName() {
        return customerName;
    }

    @Override
    public boolean addFoodItem(String item) {
        String menuItem = findOnMenu(item);
        if (menuItem == null) {
            return false;
        }
        cart.add(menuItem);
        return true;
    }

    @Override
    public boolean removeFoodItem(String item) {
        String menuItem = findOnMenu(item);
        return menuItem != null && cart.remove(menuItem);
    }

    @Override
    public List<String> viewCart() {
        return new ArrayList<>(cart); // defensive copy
    }

    @Override
    public void clearCart() {
        cart.clear();
    }

    @Remove
    @Override
    public void checkout() {
        cart.clear(); // container destroys the bean after this method returns
    }

    // Case-insensitive match against the menu; returns the canonical name or null
    private String findOnMenu(String item) {
        if (item == null) {
            return null;
        }
        for (String m : MENU) {
            if (m.equalsIgnoreCase(item.trim())) {
                return m;
            }
        }
        return null;
    }
}
