package bg.sofia.uni.fmi.mjt.trading.price;

import bg.sofia.uni.fmi.mjt.trading.stock.AmazonStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.GoogleStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.MicrosoftStockPurchase;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceChart implements PriceChartAPI{
    double microsoftStockPrice, googleStockPrice, amazonStockPrice;

    public PriceChart(double microsoftStockPrice, double googleStockPrice, double amazonStockPrice) {
        this.microsoftStockPrice = microsoftStockPrice;
        this.amazonStockPrice = amazonStockPrice;
        this.googleStockPrice = googleStockPrice;
    }

    @Override
    public double getCurrentPrice(String stockTicker) {
        return switch(stockTicker) {
            case AmazonStockPurchase.STOCK_TICKER -> BigDecimal.valueOf(amazonStockPrice).setScale(2, RoundingMode.HALF_UP).doubleValue();
//            case AmazonStockPurchase.STOCK_TICKER -> amazonStockPrice;
            case GoogleStockPurchase.STOCK_TICKER -> BigDecimal.valueOf(googleStockPrice).setScale(2, RoundingMode.HALF_UP).doubleValue();
//            case GoogleStockPurchase.STOCK_TICKER -> googleStockPrice;
            case MicrosoftStockPurchase.STOCK_TICKER -> BigDecimal.valueOf(microsoftStockPrice).setScale(2, RoundingMode.HALF_UP).doubleValue();
//            case MicrosoftStockPurchase.STOCK_TICKER -> microsoftStockPrice;
            default -> 0.0;
        };
    }

    @Override
    public boolean changeStockPrice(String stockTicker, int percentChange) {
        if (percentChange < 0 ) return false;

        return switch(stockTicker) {
            case AmazonStockPurchase.STOCK_TICKER -> {
                amazonStockPrice += amazonStockPrice*percentChange;
                yield true;
            }
            case GoogleStockPurchase.STOCK_TICKER -> {
                googleStockPrice += googleStockPrice*percentChange;
                yield true;
            }
            case MicrosoftStockPurchase.STOCK_TICKER -> {
                microsoftStockPrice += microsoftStockPrice*percentChange;
                yield true;
            }
            default -> false;
        };
    }
}
