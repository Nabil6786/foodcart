package com.foodcart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Remove;
import javax.ejb.Stateful;

@Stateful
public class FoodCartBean implements FoodCart {

    private static final List<String> MENU =
            Arrays.asList("Pizza", "Burger", "Sandwich");

    private static final Map<String, Double> PRICES =
            new HashMap<>();

    static {
        PRICES.put("Pizza", 199.0);
        PRICES.put("Burger", 129.0);
        PRICES.put("Sandwich", 99.0);
    }

    private String customerName;

    private final List<String> cart =
            new ArrayList<>();

    @Override
    public void setCustomerName(String customerName) {
        if (customerName == null) {
            this.customerName = null;
        } else {
            this.customerName = customerName.trim();
        }
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

        return menuItem != null &&
               cart.remove(menuItem);
    }

    @Override
    public List<String> viewCart() {
        return new ArrayList<>(cart);
    }

    @Override
    public void clearCart() {
        cart.clear();
    }

    @Override
    public double getTotalAmount() {

        double total = 0.0;

        for (String item : cart) {

            Double price = PRICES.get(item);

            if (price != null) {
                total += price;
            }
        }

        return total;
    }

    @Override
    public int getCartItemCount() {
        return cart.size();
    }

    @Remove
    @Override
    public void checkout() {
        cart.clear();
    }

    private String findOnMenu(String item) {

        if (item == null) {
            return null;
        }

        for (String menuItem : MENU) {

            if (menuItem.equalsIgnoreCase(item.trim())) {
                return menuItem;
            }
        }

        return null;
    }
}