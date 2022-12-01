import commons.JDBCCredentials;
import dataManagers.DAOOrganizations;
import dataManagers.DAOProducts;
import models.Organization;
import models.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DaoOrganizationsTests {
    private static DAOOrganizations dao;
    private static final JDBCCredentials CREDS = JDBCCredentials.DEFAULT;
    private static final Organization testOrganization = new Organization("Nothing", 788887,4545);

    @BeforeAll
    public static void creatingDao() {
        try {
            var connection = DriverManager.getConnection(CREDS.url(), CREDS.login(), CREDS.password());
            dao = new DAOOrganizations(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    @DisplayName("Checking get method if entity in DataBase")
    void getMethodTestTrue() {
        var organization = new Organization("SportMaster", 234234, 3333);
        assertThat(organization, is(dao.get(organization.getIndTaxpayerNum())));
    }

    @Test
    @DisplayName("Checking get method if entity not in DataBase")
    void getMethodTestFalse() {
        Assertions.assertThrows(IllegalStateException.class, () -> dao.get(testOrganization.getIndTaxpayerNum()));
    }

    @Test
    @DisplayName("Checking all method when entities in DataBase")
    void allMethodTest() {
        var listOfOrganizations = new ArrayList<Organization>();
        listOfOrganizations.add(new Organization("StreetBeat", 123123,2222));
        listOfOrganizations.add(new Organization("SportMaster", 234234,3333));
        listOfOrganizations.add(new Organization("Decathlon", 345345,4444));
        listOfOrganizations.add(new Organization("SportTovary", 456456,5555));
        assertThat(listOfOrganizations, is(dao.all()));
    }

    @Test
    @DisplayName("Checking save method")
    void saveMethodTestTrue() {
        dao.save(testOrganization);
        assertThat(testOrganization, is(dao.get(testOrganization.getIndTaxpayerNum())));
        dao.delete(testOrganization);
    }

    @Test
    @DisplayName("Checking update method")
    void updateMethodTestTrue() {
        dao.save(testOrganization);
        testOrganization.setOrganizationName("StreetUdar");
        dao.update(testOrganization);
        assertThat(testOrganization, is(dao.get(testOrganization.getIndTaxpayerNum())));
        dao.delete(testOrganization);
    }

    @Test
    @DisplayName("Checking delete method")
    void deleteMethodTestTrue() {
        dao.save(testOrganization);
        dao.delete(testOrganization);
        Throwable throwable = Assertions.assertThrows(IllegalStateException.class,
                () -> dao.get(testOrganization.getIndTaxpayerNum()));
        assertThat(throwable.getMessage(), is("Record with indTaxpayerNum 788887 not found"));
    }
}
