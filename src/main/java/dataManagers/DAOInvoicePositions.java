package dataManagers;

import commons.JDBCCredentials;
import models.InvoicePosition;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"NotNullNullableValidation", "SqlNoDataSourceInspection", "SqlResolve"})
public class DAOInvoicePositions implements DAO<InvoicePosition> {
    private static final @NotNull
    JDBCCredentials CREDS = JDBCCredentials.DEFAULT;
    private final Connection connection;
    private final @NotNull
    String INVOICE_ID = "invoice_id";
    private final @NotNull
    String PRICE = "price";
    private final @NotNull
    String AMOUNT = "amount";
    private final @NotNull
    String PRODUCT_CODE = "product_code";
    private final @NotNull
    String ID = "id";
    private final String GET_BY_ID = "SELECT *" +
            " FROM invoice_positions WHERE id = ";

    private final String GET_ALL_INVOICE_POSITIONS = "SELECT * FROM invoice_positions";
    private final String SAVE_TO_DATABASE = "INSERT INTO invoice_positions" +
            "(id, invoice_id, price, amount, product_code) VALUES (?, ?, ?, ?, ?)";
    private final String UPDATE_DATABASE = "UPDATE invoice_positions SET " +
            "invoice_id = ?, price = ?, amount = ?, product_code = ? WHERE id = ?";
    private final String DELETE_IN_DATABASE = "DELETE FROM invoice_positions " +
            "WHERE id = (?)";


    public DAOInvoicePositions(Connection connection) {
        this.connection = connection;
    }

    @NotNull
    @Override
    public InvoicePosition get(int id) {
        try (var statement = connection.createStatement()) {
            try (var resultSet = statement.executeQuery(GET_BY_ID + id)) {
                if (resultSet.next()) {
                    return new InvoicePosition(resultSet.getInt(ID),
                            resultSet.getInt(INVOICE_ID),
                            resultSet.getInt(PRICE),
                            resultSet.getInt(AMOUNT),
                            resultSet.getInt(PRODUCT_CODE));
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        throw new IllegalStateException("Record with id " + id +
                " not found");
    }

    @NotNull
    @Override
    public List<InvoicePosition> all() {
        var invoicePositionsList = new ArrayList<InvoicePosition>();
        try (var statement = connection.createStatement()) {
            try (var resultSet = statement.executeQuery(GET_ALL_INVOICE_POSITIONS)) {
                while (resultSet.next()) {
                    invoicePositionsList.add(new InvoicePosition(resultSet.getInt(ID), resultSet.getInt(INVOICE_ID),
                            resultSet.getInt(PRICE), resultSet.getInt(AMOUNT), resultSet.getInt(PRODUCT_CODE)));
                }
                return invoicePositionsList;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new IllegalStateException("No records in table: invoices");
    }

    @Override
    public void save(@NotNull InvoicePosition model) {
        try (var prepareStatement = connection.prepareStatement(SAVE_TO_DATABASE)) {
            int fieldNumber = 1;
            prepareStatement.setInt(fieldNumber++, model.getId());
            prepareStatement.setInt(fieldNumber++, model.getInvoiceId());
            prepareStatement.setInt(fieldNumber++, model.getPrice());
            prepareStatement.setInt(fieldNumber++, model.getAmount());
            prepareStatement.setInt(fieldNumber, model.getProductCode());
            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void update(@NotNull InvoicePosition model) {
        try (var prepareStatement = connection.prepareStatement(UPDATE_DATABASE)) {
            int fieldNumber = 1;
            prepareStatement.setInt(1, model.getInvoiceId());
            prepareStatement.setInt(2, model.getPrice());
            prepareStatement.setInt(3, model.getAmount());
            prepareStatement.setInt(4, model.getProductCode());
            prepareStatement.setInt(5, model.getId());
            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void delete(@NotNull InvoicePosition model) {
        try (var prepareStatement = connection.prepareStatement(DELETE_IN_DATABASE)) {
            prepareStatement.setInt(1, model.getId());
            if (prepareStatement.executeUpdate() == 0) {
                throw new IllegalStateException("Record with Id " + model.getId() +
                        " not found");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
