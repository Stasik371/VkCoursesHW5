package controllers;

import commons.JDBCCredentials;
import lombok.Data;
import models.InvoicePosition;
import models.Organization;
import models.Product;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"NotNullNullableValidation", "SqlNoDataSourceInspection", "SqlResolve"})
public class ReportCreator {
    private static final @NotNull
    JDBCCredentials CREDS = JDBCCredentials.DEFAULT;
    private final Connection connection;

    public ReportCreator() {
        try {
            this.connection = DriverManager.getConnection(CREDS.url(), CREDS.login(), CREDS.password());
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }


    private final String GET_10_ORGANIZATIONS = "SELECT organizations.organizations_name, " +
            "organizations.ind_taxpayer_num, organizations.checking_account FROM organizations " +
            "JOIN invoices i ON organizations.ind_taxpayer_num = i.organization_num " +
            "JOIN invoice_positions ip on i.invoice_id = ip.invoice_id " +
            "JOIN products p ON p.product_code = ip.product_code " +
            "WHERE p.product_code = ? ORDER BY ip.amount DESC LIMIT 10;";

    //Выбрать первые 10 поставщиков по количеству поставленного товара
    public ArrayList<Organization> first10OrganizationsByProduct(@NotNull Product product) {
        var listOfOrganization = new ArrayList<Organization>();
        try (var statement = connection.prepareStatement(GET_10_ORGANIZATIONS)) {
            statement.setInt(1, product.getProductCode());
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    listOfOrganization.add(new Organization(
                            resultSet.getString("organizations_name"),
                            resultSet.getInt("ind_taxpayer_num"),
                            resultSet.getInt("checking_account")
                    ));
                }
                return listOfOrganization;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        throw new IllegalStateException("No records with product code " + product.getProductCode());
    }


    private final String GET_ORGANIZATION_WITH_AMOUNT_MORE = "SELECT organizations.organizations_name, " +
            "organizations.ind_taxpayer_num, organizations.checking_account " +
            "FROM organizations" +
            "         JOIN invoices i ON organizations.ind_taxpayer_num = i.organization_num " +
            "         JOIN invoice_positions ip ON i.invoice_id = ip.invoice_id " +
            "WHERE amount > ?  " +
            "  AND product_code = ? " +
            "ORDER BY amount DESC;";

    //Выбрать поставщиков с суммой поставленного товара выше указанного количества
    //(товар и его количество должны допускать множественное указание).
    public ArrayList<Organization> organizationsWithMoreAmountThenParams(@NotNull Map<Integer, Integer> mapWithProducts) {
        var listOfOrganization = new ArrayList<Organization>();
        for (Map.Entry<Integer, Integer> map : mapWithProducts.entrySet()) {
            int fieldIndex = 1;
            try (var statement = connection.prepareStatement(GET_ORGANIZATION_WITH_AMOUNT_MORE)) {
                int amount = map.getKey();
                int productCode = map.getValue();
                statement.setInt(fieldIndex++, amount);
                statement.setInt(fieldIndex, productCode);
                try (var resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        listOfOrganization.add(new Organization(
                                resultSet.getString("organizations_name"),
                                resultSet.getInt("ind_taxpayer_num"),
                                resultSet.getInt("checking_account")
                        ));
                    }
                    listOfOrganization.add(null);
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        return listOfOrganization;
    }


    public static final String SUM_PER_DAY = "SELECT date_of_invoice::DATE, p.name_of_product, " +
            "p.product_code, SUM(amount) AS amount, price " +
            "FROM products " +
            "         JOIN invoice_positions ip ON products.product_code = ip.product_code" +
            "         JOIN products p ON p.product_code = ip.product_code " +
            "         JOIN invoices i ON i.invoice_id = ip.invoice_id " +
            "WHERE date_of_invoice BETWEEN ? AND ? " +
            "group by date_of_invoice, p.name_of_product, ip.price,p.product_code, ip.price;";

    //За каждый день для каждого товара рассчитать количество и
    //сумму полученного товара в указанном периоде, посчитать итоги за период
    public HashMap<Date, List<HashMap<Product, AmountAndSum>>> getSumPerDay(Timestamp firstTimestamp, Timestamp secondTimestamp) {
        var mapForThirdReport = new HashMap<Date, List<HashMap<Product, AmountAndSum>>>();
        try (var statement = connection.prepareStatement(SUM_PER_DAY)) {
            statement.setTimestamp(1, firstTimestamp);
            statement.setTimestamp(2, secondTimestamp);
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    var date = resultSet.getDate("date_of_invoice");
                    var product = new Product(
                            resultSet.getString("name_of_product"),
                            resultSet.getInt("product_code"));
                    var amountAndSum = new AmountAndSum(
                            resultSet.getInt("amount"),
                            resultSet.getInt("price"));
                    var nestedMap = new HashMap<Product, AmountAndSum>();
                    if (!nestedMap.containsKey(date)) {
                        mapForThirdReport.put(date, new ArrayList<>());
                    }
                    nestedMap.put(product,amountAndSum);
                    mapForThirdReport.get(date).add(nestedMap);
                }
                return mapForThirdReport;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        throw new IllegalStateException("No Records in query");
    }

    public static final String AVERAGE_BETWEEN = "SELECT AVG(invoice_positions.price) as average_price " +
            "FROM invoice_positions " +
            "JOIN invoices i on invoice_positions.invoice_id = i.invoice_id " +
            "WHERE date_of_invoice BETWEEN ? AND ? AND product_code = ?";

    //Рассчитать среднюю цену по каждому товару за период
    public Integer averageValueBetween(Timestamp firstTimestamp, Timestamp secondTimestamp, @NotNull Product product) {
        try (var statement = connection.prepareStatement(AVERAGE_BETWEEN)) {
            statement.setTimestamp(1, firstTimestamp);
            statement.setTimestamp(2, secondTimestamp);
            statement.setInt(3, product.getProductCode());
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("average_price");
                } else {
                    throw new IllegalStateException("No records with product code " + product.getProductCode() + " between selected time");
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        throw new IllegalStateException("No Records in query");
    }


    private static final String getAllProductsByPeriod = "SELECT organizations_name, " +
            "organization_num, checking_account, name_of_product, ip.product_code " +
            "FROM products JOIN invoice_positions ip ON products.product_code = ip.product_code " +
            "FULL OUTER JOIN invoices i ON i.invoice_id = ip.invoice_id AND date_of_invoice BETWEEN ? AND ? " +
            "JOIN organizations o ON o.ind_taxpayer_num = i.organization_num " +
            "ORDER BY organization_num DESC";

    //Вывести список товаров, поставленных организациями за период.
    //Если организация товары не поставляла, то она все равно должна быть отражена в списке.
    public HashMap<Organization, ArrayList<Product>> getAllProductByPeriod(Timestamp firstTimestamp, Timestamp secondTimestamp) {
        var organizationProductMap = new HashMap<Organization, ArrayList<Product>>();
        try (var statement = connection.prepareStatement(getAllProductsByPeriod)) {
            statement.setTimestamp(1, firstTimestamp);
            statement.setTimestamp(2, secondTimestamp);
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    var organization = new Organization(resultSet.getString("organizations_name"),
                            resultSet.getInt("organization_num"),
                            resultSet.getInt("checking_account"));
                    if (!organizationProductMap.containsKey(organization)) {
                        organizationProductMap.put(organization, new ArrayList<Product>());
                    }
                    Product product = null;
                    if (resultSet.getString("name_of_product") != null) {
                        product = new Product(
                                resultSet.getString("name_of_product"),
                                resultSet.getInt("product_code"));
                    }
                    if (product != null) {
                        organizationProductMap.get(organization).add(product);
                    }
                }
                return organizationProductMap;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        throw new IllegalStateException("No Records in query");
    }
}

