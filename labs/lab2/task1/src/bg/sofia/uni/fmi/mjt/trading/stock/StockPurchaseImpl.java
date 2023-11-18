package bg.sofia.uni.fmi.mjt.trading.stock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public abstract class StockPurchaseImpl implements StockPurchase {

    int quantity;
    LocalDateTime purchaseTimestamp;
    double purchasePricePerUnit;

    public StockPurchaseImpl(int quantity, LocalDateTime purchaseTimestamp, double purchasePricePerUnit) {
        this.quantity = quantity;
        this.purchaseTimestamp = purchaseTimestamp;
        this.purchasePricePerUnit = purchasePricePerUnit;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public LocalDateTime getPurchaseTimestamp() {
        return purchaseTimestamp;
    }

    @Override
    public double getPurchasePricePerUnit() {
        return BigDecimal.valueOf(purchasePricePerUnit).setScale(2, RoundingMode.HALF_UP).doubleValue();
//        return purchasePricePerUnit;
    }

    @Override
    public double getTotalPurchasePrice() {
        double totalPrice = quantity*purchasePricePerUnit;
        return BigDecimal.valueOf(totalPrice).setScale(2, RoundingMode.HALF_UP).doubleValue();
//        return totalPrice;
    }

}
