package dataManagers;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public interface DAO<T> {

    @NotNull T get(int id) throws SQLException;

    @NotNull List<T> all();

    void save(@NotNull T model);

    void update(@NotNull T model);

    void delete(@NotNull T model);
}
