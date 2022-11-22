package controllers;

import commons.FlyWayInitializer;
import commons.JDBCCredentials;
import dataManagers.DAOInvoicePositions;
import dataManagers.DAOOrganizations;
import dataManagers.DAOProducts;
import models.Organization;
import models.Product;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Application {
    private static final @NotNull
    JDBCCredentials CREDS = JDBCCredentials.DEFAULT;

    public static void main(String[] args) throws SQLException {
        FlyWayInitializer.initDB();
        ReportCreator reportCreator = new ReportCreator();

        System.out.println("First Report");
        Product product = new Product("T-Shirt Nike", 1201); //hardcode
        System.out.println("organizations_name | ind_taxpayer_num | checking_account");
        System.out.println("-------------------+------------------+------------------");
        for (Organization org : reportCreator.first10OrganizationsByProduct(product)) {
            System.out.println(org.getOrganizationName()
                    + "\t\t\t" + org.getIndTaxpayerNum()
                    + "\t\t\t   " + org.getCheckingAccount());
        }
        System.out.println("-------------------+------------------+------------------\n");

        System.out.println("Second Report");
        Map<Integer, Integer> hashMapAmountProductCode = new HashMap<>();
        hashMapAmountProductCode.put(149, 1201);
        hashMapAmountProductCode.put(150, 1400);
        ArrayList<Organization> meow = reportCreator.organizationsWithMoreAmountThenParams(hashMapAmountProductCode);
        System.out.println("organizations_name | ind_taxpayer_num | checking_account");
        System.out.println("-------------------+------------------+------------------");
        for (Organization org : meow) {
            if (org == null) {
                System.out.println("-------------------+------------------+------------------");
                continue;
            }
            System.out.println(org.getOrganizationName()
                    + "\t\t\t" + org.getIndTaxpayerNum()
                    + "\t\t\t   " + org.getCheckingAccount());
        }
        System.out.println();

        System.out.println("Third Report");
        Timestamp firstTimeStamp = new Timestamp(1_657_443_700_000L);
        Timestamp secondTimeStamp = new Timestamp(1_668_081_600_001L);
        var mapForThirdReport = reportCreator.getSumPerDay(firstTimeStamp, secondTimeStamp);
        System.out.println("date_of_invoice | name_of_product | product_code | amount | price_for_one | price");
        System.out.println("----------------+-----------------+--------------+--------+---------------+---");
        for (Map.Entry<Date, List<HashMap<Product, AmountAndSum>>> entry : mapForThirdReport.entrySet()) {
            System.out.print(entry.getKey());
            var listOfNestedMaps = entry.getValue();
            for (Map<Product, AmountAndSum> nestedEntry : listOfNestedMaps) {
                for (Map.Entry<Product, AmountAndSum> nestedEntryMap : nestedEntry.entrySet()) {
                    System.out.print("\t\t " + nestedEntryMap.getKey().getNameOfProduct() +
                            "\t\t   " + nestedEntryMap.getKey().getProductCode() +
                            "\t\t\t " + nestedEntryMap.getValue().getAmount() +
                            "\t\t" + nestedEntryMap.getValue().getPrice() +
                            "\t\t" + nestedEntryMap.getValue().getPrice()*
                            nestedEntryMap.getValue().getAmount() + "\n");
                }
            }
        }
        System.out.println("----------------+-----------------+--------------+--------+---------------+---");

        System.out.println("\nFourth Report");
        Integer averagePrice = reportCreator.averageValueBetween(firstTimeStamp, secondTimeStamp, product);
        System.out.println("| average_price |");
        System.out.println("-------------------");
        System.out.println(averagePrice);
        System.out.println("-------------------\n");


        System.out.println("Fifth Report");
        var mapForFifthReport = reportCreator.getAllProductByPeriod(firstTimeStamp, secondTimeStamp);
        System.out.println("-------------------+------------------+------------------");
        for (Map.Entry<Organization, ArrayList<Product>> entry :
                mapForFifthReport.entrySet()) {
            System.out.println("organizations_name | ind_taxpayer_num | checking_account");
            System.out.println(entry.getKey().getOrganizationName()
                    + "\t\t\t" + entry.getKey().getIndTaxpayerNum()
                    + "\t\t\t   " + entry.getKey().getCheckingAccount());
            System.out.println("\n");
            if (entry.getValue().size() < 1) {
                System.out.println("Organization didn't deliver products during the period");
            } else {
                System.out.println("product_name | product_code ");
                for (Product productInMap : entry.getValue()) {
                    System.out.println(productInMap.getNameOfProduct() + "\t" + productInMap.getProductCode());
                }
            }
            System.out.println("-------------------+------------------+------------------");
            System.out.println("-------------------+------------------+------------------\n");
        }
    }
}

