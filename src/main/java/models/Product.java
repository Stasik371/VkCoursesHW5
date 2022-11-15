package models;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class Product {
    private @NotNull
    String nameOfProduct;
    private final int productCode;
}
