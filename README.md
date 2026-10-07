   # Online Food Cart

   Java EE application using a Stateful Session Bean (@Stateful) to keep a customer's cart across requests.

   ## Run
   mvn package payara-micro:start

   ## Try
   http://localhost:8080/foodcart/cart?action=name&value=YourName
   http://localhost:8080/foodcart/cart?action=add&value=Pizza
   http://localhost:8080/foodcart/cart?action=view
