package com.hrushev.controllers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrushev.dao.DAOConnection;
import com.hrushev.dao.OracleDAOConnection;
import com.hrushev.model.Product;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class OrderController {

    @RequestMapping(value = "/confirm", method = RequestMethod.POST)
    public String confirm(Model model, @RequestParam String username, @RequestParam String password, @RequestParam String address, @CookieValue(name = "shoppingCart") String products) {
        DAOConnection connection = OracleDAOConnection.getInstance();
        List<Product> productList = parseProductList(products);

        int userId = connection.addUser(username, password, address);
        model.addAttribute("userId", userId);

        float sum = 0;

        for (Product product: productList) {
            sum += product.getPrice();
        }

        int orderId = connection.createOrder(sum, userId);
        model.addAttribute("orderId", orderId);

        for (Product product: productList) {
            connection.addOrderDetails(product.getName(), product.getPrice(), product.getId(), orderId);
        }

        return "orderComplete";
    }

    @RequestMapping(value = "/order")
    public String confirmOrder(Model model, @CookieValue(name = "shoppingCart") String productList) {
        model.addAttribute("productList", parseProductList(productList));

        return "orderConfirmation";
    }

    private List<Product> parseProductList(String products) {
        List<Product> productList = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

            JsonNode listNode = mapper.readTree(products);

            for (int i = 0; i < listNode.size(); i++) {
                JsonNode productNode = listNode.get(i);

                int id = productNode.get("id").asInt();
                String name = productNode.get("name").asText();
                double price = productNode.get("price").asDouble();
                String desc = productNode.get("description").asText();

                productList.add(new Product(id, name, price, desc));
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return productList;
    }
}
