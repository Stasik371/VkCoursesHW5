import commons.FlyWayInitializer;
import commons.JDBCCredentials;
import dataManagers.DAOProducts;
import models.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAOProductTests {
    private static DAOProducts dao;
    private static final JDBCCredentials CREDS = JDBCCredentials.DEFAULT;
    private static final Product testProduct = new Product("Nothning", 9999);

    @BeforeAll
    public static void creatingDao() {
        try {
            var connection = DriverManager.getConnection(CREDS.url(), CREDS.login(), CREDS.password());
            dao = new DAOProducts(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    @DisplayName("Checking get method if entity in DataBase")
    void getMethodTestTrue() {
        Product product = new Product("T-shirt Adidas", 1200);
        assertThat(product, is(dao.get(product.getProductCode())));
    }

    @Test
    @DisplayName("Checking get method if entity not in DataBase")
    void getMethodTestFalse() {
        Assertions.assertThrows(IllegalStateException.class, () -> dao.get(testProduct.getProductCode()));
    }

    @Test
    @DisplayName("Checking all method when entities in DataBase")
    void allMethodTest() {
        var listOfProducts = new ArrayList<Product>();
        listOfProducts.add(new Product("T-shirt Adidas", 1200));
        listOfProducts.add(new Product("T-shirt Nike", 1201));
        listOfProducts.add(new Product("Sneakers Nike", 1300));
        listOfProducts.add(new Product("Sneakers Reebok", 1301));
        listOfProducts.add(new Product("Jacket Reebok", 1400));
        listOfProducts.add(new Product("Jacket  Puma", 1401));
        assertThat(listOfProducts, is(dao.all()));
    }

    @Test
    @DisplayName("Checking save method")
    void saveMethodTestTrue() {
        dao.save(testProduct);
        assertThat(testProduct, is(dao.get(testProduct.getProductCode())));
        dao.delete(testProduct);
    }

    @Test
    @DisplayName("Checking update method")
    void updateMethodTestTrue() {
        dao.save(testProduct);
        testProduct.setNameOfProduct("Abibas");
        dao.update(testProduct);
        assertThat(testProduct, is(dao.get(testProduct.getProductCode())));
        dao.delete(testProduct);

    }

    @Test
    @DisplayName("Checking delete method")
    void deleteMethodTestTrue() {
        dao.save(testProduct);
        dao.delete(testProduct);
        Throwable throwable = Assertions.assertThrows(IllegalStateException.class,
                () -> dao.get(testProduct.getProductCode()));
        assertThat(throwable.getMessage(), is("Record with productCode 9999 not found"));
    }
}
