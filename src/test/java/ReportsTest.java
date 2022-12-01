import commons.FlyWayInitializer;
import commons.JDBCCredentials;
import controllers.AmountAndSum;
import controllers.ReportCreator;
import models.Organization;
import models.Product;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;

public class ReportsTest {
    private static ReportCreator rc;

    @BeforeAll
    public static void creatingReportCreator() {
        FlyWayInitializer.initDB();
        rc = new ReportCreator();
    }

    @Test
    @DisplayName("Выбрать первые 10 поставщиков по количеству поставленного товара")
    void firstTenOrganizations() {
        var testProduct = new Product("T-shirt Nike", 1201);
        var listOfOrganization = new ArrayList<Organization>();
        listOfOrganization.add(new Organization("SportTovary", 456456, 5555));
        listOfOrganization.add(new Organization("StreetBeat", 123123, 2222));
        assertThat(listOfOrganization, is(rc.first10OrganizationsByProduct(testProduct)));
    }

    @Test
    @DisplayName("Выбрать поставщиков с количеством поставленного товара выше указанного значения.")
    void organizationsWithAmountAndPrice() {
        var hashMapAmountProductCode = new HashMap<Integer, Integer>();
        hashMapAmountProductCode.put(150, 1400);
        var listOfOrganization = new ArrayList<Organization>();
        listOfOrganization.add(new Organization("SportMaster", 234234, 3333));
        listOfOrganization.add(new Organization("Decathlon", 345345, 4444));
        listOfOrganization.add(null);
        assertThat(listOfOrganization, is(rc.organizationsWithMoreAmountThenParams(hashMapAmountProductCode)));
    }

    @Test
    @DisplayName("За каждый день для каждого товара рассчитать количество " +
            "и сумму полученного товара в указанном периоде, посчитать итоги за период")
    void sumPerDay() {
        Timestamp firstTimeStamp = new Timestamp(1_660_129_200_000L);
        Timestamp secondTimeStamp = new Timestamp(1_668_081_500_000L);
        var mapForReport = new HashMap<Date, List<HashMap<Product, AmountAndSum>>>();
        var date1 = new Date(1_660_129_200_000L);
        var date2 = new Date(1_662_814_800_000L);
        var map1 = new HashMap<Product, AmountAndSum>();
        map1.put(new Product("Jacket Reebok", 1400), new AmountAndSum(200, 11999));
        var map2 = new HashMap<Product, AmountAndSum>();
        map2.put(new Product("Jacket Reebok", 1400), new AmountAndSum(400, 12999));
        var list1 = new ArrayList<HashMap<Product, AmountAndSum>>();
        list1.add(map1);
        var list2 = new ArrayList<HashMap<Product, AmountAndSum>>();
        list2.add(map2);
        mapForReport.put(date1, list1);
        mapForReport.put(date2, list2);
        assertThat(mapForReport, is(rc.getSumPerDay(firstTimeStamp, secondTimeStamp)));
    }


    @Test
    @DisplayName("Рассчитать среднюю цену по каждому товару за период")
    void avgByPeriod() {
        Timestamp firstTimeStamp = new Timestamp(1_660_129_200_000L);
        Timestamp secondTimeStamp = new Timestamp(1_668_081_500_000L);
        assertThat(12499, is(rc.averageValueBetween(firstTimeStamp, secondTimeStamp, new Product("Jacket Reebok", 1400))));
    }

    @Test
    @DisplayName("Вывести список товаров, поставленных организациями за период. " +
            "Если организация товары не поставляла, то она все равно должна быть отражена в списке")
    void listOfProductsByPeriod() {
        Timestamp firstTimeStamp = new Timestamp(1_657_444_600_000L);
        Timestamp secondTimeStamp = new Timestamp(1_660_129_200_000L);
        var mapOrganizationProduct = new HashMap<Organization, ArrayList<Product>>();
        mapOrganizationProduct.put(new Organization("SportMaster",
                234234, 3333), new ArrayList<Product>());
        mapOrganizationProduct.put(new Organization("StreetBeat",
                123123, 2222), new ArrayList<Product>());
        var list3 = new ArrayList<Product>();
        list3.add(new Product("Jacket Reebok", 1400));
        list3.add(new Product("Jacket  Puma", 1401));
        mapOrganizationProduct.put(new Organization("Decathlon",
                345345, 4444), list3);
        mapOrganizationProduct.put(new Organization("SportTovary",
                456456, 5555), new ArrayList<Product>());
        assertThat(mapOrganizationProduct, is(rc.getAllProductByPeriod(firstTimeStamp, secondTimeStamp)));
    }

}

