package controllers;

import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;

public class FlyWayInitializer {
    public static final @NotNull
    String CONNECTION = "jdbc:postgresql://127.0.0.1:5432/";
    public static final @NotNull
    String DB_NAME = "db2";
    public static final @NotNull
    String USERNAME = "postgres";
    public static final @NotNull
    String PASSWORD = "postgre";

    public static void connectToDataBase() {
        final Flyway flyway = Flyway
                .configure()
                .dataSource(CONNECTION + DB_NAME, USERNAME, PASSWORD)
                .locations("sql")
                .cleanDisabled(false)
                .load();
        flyway.clean();
        flyway.migrate();
        System.out.println("Vse ok");
    }
}
