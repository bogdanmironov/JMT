package bg.sofia.uni.fmi.mjt.trading.stock;

import java.time.LocalDateTime;

public class MicrosoftStockPurchase extends StockPurchaseImpl{
    public static final String STOCK_TICKER = "MSFT";

    public MicrosoftStockPurchase(int quantity, LocalDateTime purchaseTimestamp, double purchasePricePerUnit) {
        super(quantity, purchaseTimestamp, purchasePricePerUnit);
    }

    @Override
    public String getStockTicker() {
        return STOCK_TICKER;
    }
}
