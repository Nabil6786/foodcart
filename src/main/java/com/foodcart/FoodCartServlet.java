package com.foodcart;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Demo client. The stateful bean is looked up once per HTTP session and kept
 * in the session, so every later request talks to the SAME bean instance.
 *
 * Try (in one browser tab):
 *   /cart?action=name&value=Nabeel
 *   /cart?action=add&value=Pizza
 *   /cart?action=add&value=Burger
 *   /cart?action=view          -> still shows Pizza, Burger (state maintained)
 *   /cart?action=remove&value=Pizza
 *   /cart?action=clear
 */
@WebServlet("/cart")
public class FoodCartServlet extends HttpServlet {

    private static final String CART_KEY = "foodCart";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        FoodCart cart = getCart(req.getSession());
        String action = req.getParameter("action");
        String value = req.getParameter("value");
        String message = "";

        if (action != null) {
            switch (action) {
                case "name":
                    cart.setCustomerName(value);
                    message = "Customer name set to " + value;
                    break;
                case "add":
                    message = cart.addFoodItem(value)
                            ? value + " added to cart."
                            : "'" + value + "' is not on the menu (Pizza, Burger, Sandwich).";
                    break;
                case "remove":
                    message = cart.removeFoodItem(value)
                            ? value + " removed from cart."
                            : value + " was not in the cart.";
                    break;
                case "clear":
                    cart.clearCart();
                    message = "Cart cleared.";
                    break;
                case "checkout":
                    cart.checkout();
                    req.getSession().removeAttribute(CART_KEY);
                    message = "Checked out. Bean removed.";
                    break;
                case "view":
                default:
                    break;
            }
        }

        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<html><body>");
            out.println("<h2>Online Food Cart</h2>");
            if (!message.isEmpty()) {
                out.println("<p><b>" + escape(message) + "</b></p>");
            }
            if (req.getSession(false) != null && req.getSession().getAttribute(CART_KEY) != null) {
                String name = cart.getCustomerName();
                out.println("<p>Customer: " + (name == null ? "(not set)" : escape(name)) + "</p>");
                List<String> items = cart.viewCart();
                out.println("<p>Items in cart (" + items.size() + "):</p><ul>");
                for (String item : items) {
                    out.println("<li>" + escape(item) + "</li>");
                }
                out.println("</ul>");
            }
            out.println("<p>Menu: Pizza, Burger, Sandwich</p>");
            out.println("<p>Actions: ?action=name&amp;value=YourName | add | remove | view | clear | checkout</p>");
            out.println("</body></html>");
        }
    }

    // Look up the stateful bean once per HTTP session and reuse it
    private FoodCart getCart(HttpSession session) throws ServletException {
        FoodCart cart = (FoodCart) session.getAttribute(CART_KEY);
        if (cart == null) {
            try {
                cart = (FoodCart) new InitialContext().lookup("java:module/FoodCartBean");
                session.setAttribute(CART_KEY, cart);
            } catch (NamingException e) {
                throw new ServletException("Could not look up FoodCartBean", e);
            }
        }
        return cart;
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
