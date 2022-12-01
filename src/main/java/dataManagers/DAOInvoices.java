package dataManagers;

import commons.JDBCCredentials;
import models.Invoice;
import models.Organization;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"NotNullNullableValidation", "SqlNoDataSourceInspection", "SqlResolve"})
public class DAOInvoices implements DAO<Invoice> {
    private static final @NotNull
    JDBCCredentials CREDS = JDBCCredentials.DEFAULT;
    private final Connection connection;
    private final @NotNull
    String INVOICE_ID = "invoice_id";
    private final @NotNull
    String DATE_OF_INVOICE = "date_of_invoice";
    private final @NotNull
    String ORGANIZATION_NUM = "organization_num";
    private final String GET_BY_ID = "SELECT invoice_id, date_of_invoice, organization_num" +
            " FROM invoices WHERE invoice_id = ";
    private final String GET_ALL_INVOICES = "SELECT * FROM invoices";
    private final String SAVE_TO_DATABASE = "INSERT INTO invoices" +
            "(invoice_id, date_of_invoice, organization_num) VALUES (?, ?, ?)";
    private final String UPDATE_DATABASE = "UPDATE invoices SET " +
            "date_of_invoice = ?, organization_num = ? WHERE invoice_id = (?)";

    private final String DELETE_IN_DATABASE = "DELETE FROM invoices WHERE invoice_id = (?)";

    public DAOInvoices(Connection connection) {
        this.connection = connection;
    }


    @NotNull
    @Override
    public Invoice get(int invoiceId) {
        try (var statement = connection.createStatement()) {
            try (var resultSet = statement.executeQuery(GET_BY_ID + invoiceId)) {
                if (resultSet.next()) {
                    return new Invoice(resultSet.getInt(INVOICE_ID),
                            resultSet.getTimestamp(DATE_OF_INVOICE), resultSet.getInt(ORGANIZATION_NUM));
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        throw new IllegalStateException("Record with invoiceId " + invoiceId + " not found");
    }

    @NotNull
    @Override
    public List<Invoice> all() {
        var invoiceList = new ArrayList<Invoice>();
        try (var statement = connection.createStatement()) {
            try (var resultSet = statement.executeQuery(GET_ALL_INVOICES)) {
                while (resultSet.next()) {
                    invoiceList.add(new Invoice(resultSet.getInt(INVOICE_ID),
                            resultSet.getTimestamp(DATE_OF_INVOICE), resultSet.getInt(ORGANIZATION_NUM)));
                }
                return invoiceList;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new IllegalStateException("No records in table: invoices");
    }

    @Override
    public void save(@NotNull Invoice model) {
        try (var prepareStatement = connection.prepareStatement(SAVE_TO_DATABASE)) {
            int fieldNumber = 1;
            prepareStatement.setInt(fieldNumber++, model.getInvoiceId());
            prepareStatement.setTimestamp(fieldNumber++, model.getDate());
            prepareStatement.setInt(fieldNumber, model.getOrganizationNum());
            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void update(@NotNull Invoice model) {
        try (var prepareStatement = connection.prepareStatement(UPDATE_DATABASE)) {
            int fieldNumber = 1;
            prepareStatement.setInt(fieldNumber++, model.getInvoiceId());
            prepareStatement.setTimestamp(fieldNumber++, model.getDate());
            prepareStatement.setInt(fieldNumber, model.getOrganizationNum());
            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void delete(@NotNull Invoice model) {
        try (var prepareStatement = connection.prepareStatement(DELETE_IN_DATABASE)) {
            prepareStatement.setInt(1, model.getInvoiceId());
            if (prepareStatement.executeUpdate() == 0) {
                throw new IllegalStateException("Record with Invoice_id = " + model.getInvoiceId() + " not found");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
