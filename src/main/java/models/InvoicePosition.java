package models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class InvoicePosition {
    private int id;
    private int invoiceId;
    private int price;
    private int amount;
    private int productCode;

}
