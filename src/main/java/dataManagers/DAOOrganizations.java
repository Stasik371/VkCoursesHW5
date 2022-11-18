package dataManagers;

import commons.JDBCCredentials;
import models.Organization;
import models.Product;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"NotNullNullableValidation", "SqlNoDataSourceInspection", "SqlResolve"})
public class DAOOrganizations implements DAO<Organization> {
    private static final @NotNull
    JDBCCredentials CREDS = JDBCCredentials.DEFAULT;
    private final Connection connection;
    private final @NotNull
    String NAME_OF_ORGANIZATION = "organizations_name";
    private final @NotNull
    String IDENTIFICATION_TAXPAYER_NUMBER = "ind_taxpayer_num";
    private final @NotNull
    String CHECKING_ACCOUNT = "checking_account";
    private final String GET_BY_ID = "SELECT organizations_name, ind_taxpayer_num, checking_account" +
            " FROM organizations WHERE ind_taxpayer_num = ";
    private final String GET_ALL_ORGANIZATIONS = "SELECT * FROM organizations";
    private final String SAVE_TO_DATABASE = "INSERT INTO organizations" +
            "(organizations_name, ind_taxpayer_num,checking_account) VALUES (?, ?, ?)";
    private final String UPDATE_DATABASE = "UPDATE organizations SET organizations_name = ?, checking_account = ? WHERE ind_taxpayer_num = (?)";
    private final String DELETE_IN_DATABASE = "DELETE FROM organizations WHERE ind_taxpayer_num = (?)";

    public DAOOrganizations(Connection connection) {
        this.connection = connection;
    }

    @NotNull
    @Override
    public Organization get(int indTaxpayerNum) {
        try (var statement = connection.createStatement()) {
            try (var resultSet = statement.executeQuery(GET_BY_ID + indTaxpayerNum)) {
                if (resultSet.next()) {
                    return new Organization(resultSet.getString(NAME_OF_ORGANIZATION),
                            resultSet.getInt(IDENTIFICATION_TAXPAYER_NUMBER), resultSet.getInt(CHECKING_ACCOUNT));
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        throw new IllegalStateException("Record with indTaxpayerNum " + indTaxpayerNum + " not found");
    }

    @NotNull
    @Override
    public List<Organization> all() {
        var organizationsList = new ArrayList<Organization>();
        try (var statement = connection.createStatement()) {
            try (var resultSet = statement.executeQuery(GET_ALL_ORGANIZATIONS)) {
                while (resultSet.next()) {
                    organizationsList.add(new Organization(resultSet.getString(NAME_OF_ORGANIZATION),
                            resultSet.getInt(IDENTIFICATION_TAXPAYER_NUMBER), resultSet.getInt(CHECKING_ACCOUNT)));
                }
                return organizationsList;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new IllegalStateException("No records in table: organizations");
    }

    @Override
    public void save(@NotNull Organization model) {
        try (var prepareStatement = connection.prepareStatement(SAVE_TO_DATABASE)) {
            int fieldNumber = 1;
            prepareStatement.setString(fieldNumber++, model.getOrganizationName());
            prepareStatement.setInt(fieldNumber++, model.getIndTaxpayerNum());
            prepareStatement.setInt(fieldNumber, model.getCheckingAccount());
            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void update(@NotNull Organization model) {
        try (var prepareStatement = connection.prepareStatement(UPDATE_DATABASE)) {
            int fieldNumber = 1;
            prepareStatement.setString(fieldNumber++, model.getOrganizationName());
            prepareStatement.setInt(fieldNumber++, model.getCheckingAccount());
            prepareStatement.setInt(fieldNumber, model.getIndTaxpayerNum());
            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void delete(@NotNull Organization model) {
        try (var prepareStatement = connection.prepareStatement(DELETE_IN_DATABASE)) {
            prepareStatement.setInt(1, model.getIndTaxpayerNum());
            if (prepareStatement.executeUpdate() == 0) {
                throw new IllegalStateException("Record with ind_taxpayer_num = " + model.getIndTaxpayerNum() + " not found");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
