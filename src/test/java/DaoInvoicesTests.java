import commons.JDBCCredentials;
import dataManagers.DAOInvoices;
import dataManagers.DAOProducts;
import models.Invoice;
import models.InvoicePosition;
import models.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DaoInvoicesTests {
    private static DAOInvoices dao;
    private static final JDBCCredentials CREDS = JDBCCredentials.DEFAULT;
    private static final Timestamp testTimestamp = new Timestamp(1_657_108_800_000L);
    private static final Invoice testInvoice = new Invoice(10, testTimestamp, 123123);

    @BeforeAll
    public static void creatingDao() {
        try {
            var connection = DriverManager.getConnection(CREDS.url(), CREDS.login(), CREDS.password());
            dao = new DAOInvoices(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    @Test
    @DisplayName("Checking get method if entity in DataBase")
    void getMethodTestTrue() {
        Timestamp timestamp = new Timestamp(1_668_081_600_000L);
        Invoice invoice = new Invoice(1, timestamp, 123123);
        assertThat(invoice, is(dao.get(invoice.getInvoiceId())));
    }

    @Test
    @DisplayName("Checking get method if entity not in DataBase")
    void getMethodTestFalse() {
        Assertions.assertThrows(IllegalStateException.class, () -> dao.get(testInvoice.getInvoiceId()));
    }

    @Test
    @DisplayName("Checking all method when entities in DataBase")
    void allMethodTest() {
        var listOfInvoices = new ArrayList<Invoice>();
        listOfInvoices.add(new Invoice(1, new Timestamp(1_668_081_600_000L), 123123));
        listOfInvoices.add(new Invoice(2,new Timestamp(1_662_814_800_000L),234234));
        listOfInvoices.add(new Invoice(3,new Timestamp(1_660_129_200_000L),345345));
        listOfInvoices.add(new Invoice(4,new Timestamp(1_657_443_600_000L),456456));
        assertThat(listOfInvoices, is(dao.all()));
    }

    @Test
    @DisplayName("Checking save method")
    void saveMethodTestTrue() {
        dao.save(testInvoice);
        assertThat(testInvoice, is(dao.get(testInvoice.getInvoiceId())));
        dao.delete(testInvoice);
    }

    @Test
    @DisplayName("Checking update method")
    void updateMethodTestTrue() {
        var invoice = new Invoice(1, new Timestamp(1_668_081_600_000L), 123123);
        dao.update(invoice);
        assertThat(invoice, is(dao.get(invoice.getInvoiceId())));
        var invoice2 = new Invoice(1, new Timestamp(1_668_081_600_000L), 123123);
        dao.update(invoice2);
    }

    @Test
    @DisplayName("Checking delete method")
    void deleteMethodTestTrue() {
        dao.save(testInvoice);
        dao.delete(testInvoice);
        Throwable throwable = Assertions.assertThrows(IllegalStateException.class,
                () -> dao.get(testInvoice.getInvoiceId()));
        assertThat(throwable.getMessage(), is("Record with invoiceId 10 not found"));
    }
}
