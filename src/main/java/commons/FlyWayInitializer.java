package commons;

import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;

public final class FlyWayInitializer {
    private static final @NotNull
    JDBCCredentials CREDS = JDBCCredentials.DEFAULT;

    public static void initDB() {
        final Flyway flyway = Flyway
                .configure()
                .dataSource(CREDS.url(), CREDS.login(), CREDS.password())
                .locations("sql")
                .cleanDisabled(false)
                .loggers()
                .load();
        flyway.clean();
        flyway.migrate();
    }
}
