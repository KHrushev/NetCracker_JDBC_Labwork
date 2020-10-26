package com.hrushev.controllers;

import com.hrushev.dao.DAOConnection;
import com.hrushev.dao.OracleDAOConnection;
import com.hrushev.model.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.List;

@Controller
public class HomeController {
    @RequestMapping("/")
    public String home(Model model) {
        try {
            DAOConnection daoConnection = OracleDAOConnection.getInstance();
            List<Product> productList = daoConnection.getAllProducts();

            model.addAttribute("sqlErrorMarker", "false");
            model.addAttribute("productList", productList);
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("sqlErrorMarker", "true");
        }

        return "home";
    }

    @RequestMapping("/filter")
    public String filter(Model model, @RequestParam(name = "filter-price", defaultValue = "Default") String priceFilter, @RequestParam(name = "filter-category", defaultValue = "Default") String categoryFilter) {
        DAOConnection daoConnection = OracleDAOConnection.getInstance();

        List<Product> productList = null;

        try {
            productList = daoConnection.getFilteredProducts(priceFilter, categoryFilter);
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("sqlErrorMarker", "true");
        }

        model.addAttribute("productList", productList);

        return "card-template";
    }
}
