FROM payara/micro:5.2022.5

COPY target/foodcart.war /opt/payara/deployments/foodcart.war

CMD ["--port", "8080"]