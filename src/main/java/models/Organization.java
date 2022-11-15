package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
public class Organization {
    private @NotNull
    String organizationName;
    private int indTaxpayerNum;
    private int checkingAccount;
}
