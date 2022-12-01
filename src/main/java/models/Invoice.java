package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class Invoice {
    private final int invoiceId;
    private @NotNull
    Timestamp date;
    private int organizationNum;

}
