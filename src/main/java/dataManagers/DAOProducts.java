package dataManagers;

import commons.JDBCCredentials;
import models.Product;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"NotNullNullableValidation", "SqlNoDataSourceInspection", "SqlResolve"})
public class DAOProducts implements DAO<Product> {
    private static final @NotNull
    JDBCCredentials CREDS = JDBCCredentials.DEFAULT;
    private final Connection connection;
    private final @NotNull
    String NAME_OF_PRODUCT = "name_of_product";
    private final @NotNull
    String PRODUCT_CODE = "product_code";
    private final String GET_BY_ID = "SELECT product_code, name_of_product FROM products WHERE product_code = ";
    private final String GET_ALL_PRODUCTS = "SELECT * FROM products";
    private final String SAVE_TO_DATABASE = "INSERT INTO products(name_of_product, product_code) VALUES (?, ?)";
    private final String DELETE_IN_DATABASE = "DELETE FROM products WHERE product_code = (?)";
    private final String UPDATE_DATABASE = "UPDATE products SET name_of_product = ? WHERE product_code = (?)";


    public DAOProducts(Connection connection) {
        this.connection = connection;
    }

    @NotNull
    @Override
    public Product get(int productCode) {
        try (var statement = connection.createStatement()) {
            try (var resultSet = statement.executeQuery(GET_BY_ID + productCode)) {
                if (resultSet.next()) {
                    return new Product(resultSet.getString(NAME_OF_PRODUCT), resultSet.getInt(PRODUCT_CODE));
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        throw new IllegalStateException("Record with productCode " + productCode + " not found");
    }

    @NotNull
    @Override
    public List<Product> all() {
        var productList = new ArrayList<Product>();
        try (var statement = connection.createStatement()) {
            try (var resultSet = statement.executeQuery(GET_ALL_PRODUCTS)) {
                while (resultSet.next()) {
                    productList.add(new Product(resultSet.getString(NAME_OF_PRODUCT), resultSet.getInt(PRODUCT_CODE)));
                }
                return productList;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new IllegalStateException("No records in table: products");
    }

    @Override
    public void save(@NotNull Product model) {
        try (var preparedStatement = connection.prepareStatement(SAVE_TO_DATABASE)) {
            int fieldNumber = 1;
            preparedStatement.setString(fieldNumber++, model.getNameOfProduct());
            preparedStatement.setInt(fieldNumber, model.getProductCode());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void update(@NotNull Product model) {
        try (var prepareStatement = connection.prepareStatement(UPDATE_DATABASE)) {
            prepareStatement.setString(1, model.getNameOfProduct());
            prepareStatement.setInt(2, model.getProductCode());
            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(@NotNull Product model) {
        try (var prepareStatement = connection.prepareStatement(DELETE_IN_DATABASE)) {
            prepareStatement.setInt(1, model.getProductCode());
            if (prepareStatement.executeUpdate() == 0) {
                throw new IllegalStateException("Record with id = " + model.getProductCode() + " not found");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}