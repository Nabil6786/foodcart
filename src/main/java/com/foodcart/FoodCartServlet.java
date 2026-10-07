package com.foodcart;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/cart")
public class FoodCartServlet extends HttpServlet {

    private static final String CART_KEY = "foodCart";

    @Override
    protected void doGet(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        FoodCart cart = getCart(req.getSession());

        String action = req.getParameter("action");
        String value = req.getParameter("value");

        resp.setContentType("text/html;charset=UTF-8");

        if (action == null || action.equals("view")) {
            sendCartResponse(cart, resp);
            return;
        }

        switch (action) {

            case "name":

                cart.setCustomerName(value);

                sendMessage(
                        resp,
                        "Welcome, " + value + "!"
                );

                break;


            case "add":

                if (cart.addFoodItem(value)) {

                    sendMessage(
                            resp,
                            value + " added to cart."
                    );

                } else {

                    sendMessage(
                            resp,
                            "Invalid food item."
                    );
                }

                break;


            case "remove":

                if (cart.removeFoodItem(value)) {

                    sendMessage(
                            resp,
                            value + " removed from cart."
                    );

                } else {

                    sendMessage(
                            resp,
                            value + " was not found in the cart."
                    );
                }

                break;


            case "clear":

                cart.clearCart();

                sendMessage(
                        resp,
                        "Cart cleared successfully."
                );

                break;


            case "checkout":

                cart.checkout();

                req.getSession().removeAttribute(CART_KEY);

                sendMessage(
                        resp,
                        "Order completed successfully."
                );

                break;


            default:

                sendMessage(
                        resp,
                        "Unknown action."
                );

                break;
        }
    }


    /**
     * Sends the current cart information
     * to the frontend.
     */
    private void sendCartResponse(FoodCart cart,
                                  HttpServletResponse resp)
            throws IOException {

        List<String> items = cart.viewCart();

        /*
         * Count quantity of each food item.
         */
        Map<String, Integer> quantities =
                new LinkedHashMap<>();

        for (String item : items) {

            quantities.put(
                    item,
                    quantities.getOrDefault(item, 0) + 1
            );
        }


        StringBuilder html = new StringBuilder();

        html.append("<div class='cart-data'>");


        // Customer name
        html.append("<div id='customerNameFromServer'>");

        if (cart.getCustomerName() != null) {
            html.append(
                    escape(cart.getCustomerName())
            );
        }

        html.append("</div>");


        // Cart items
        html.append("<ul>");

        for (Map.Entry<String, Integer> entry
                : quantities.entrySet()) {

            String item = entry.getKey();
            int quantity = entry.getValue();

            html.append("<li data-item='")
                    .append(escape(item))
                    .append("'>");

            html.append("<span class='item-name'>")
                    .append(escape(item))
                    .append("</span>");

            html.append(
                    "<span class='item-quantity'>"
            )
            .append(" × ")
            .append(quantity)
            .append("</span>");

            html.append("</li>");
        }

        html.append("</ul>");


        // Total item count
        html.append(
                "<div id='cartItemCount'>"
        );

        html.append(cart.getCartItemCount());

        html.append("</div>");


        // Total amount
        html.append(
                "<div id='cartTotal'>"
        );

        html.append(
                String.format(
                        "₹%.2f",
                        cart.getTotalAmount()
                )
        );

        html.append("</div>");


        html.append("</div>");

        resp.getWriter().write(
                html.toString()
        );
    }


    /**
     * Sends a simple message to the frontend.
     */
    private void sendMessage(HttpServletResponse resp,
                             String message)
            throws IOException {

        resp.getWriter().write(
                "<div class='server-message'>" +
                escape(message) +
                "</div>"
        );
    }


    /**
     * Gets the same Stateful Session Bean
     * for the current HTTP session.
     */
    private FoodCart getCart(HttpSession session)
            throws ServletException {

        FoodCart cart =
                (FoodCart) session.getAttribute(CART_KEY);

        if (cart == null) {

            try {

                cart = (FoodCart) new InitialContext()
                        .lookup(
                                "java:module/FoodCartBean"
                        );

                session.setAttribute(
                        CART_KEY,
                        cart
                );

            } catch (NamingException e) {

                throw new ServletException(
                        "Could not look up FoodCartBean",
                        e
                );
            }
        }

        return cart;
    }


    /**
     * Prevents HTML characters from
     * being inserted directly into the page.
     */
    private static String escape(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}