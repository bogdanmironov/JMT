package bg.sofia.uni.fmi.mjt.trading;

import bg.sofia.uni.fmi.mjt.trading.price.PriceChart;
import bg.sofia.uni.fmi.mjt.trading.price.PriceChartAPI;
import bg.sofia.uni.fmi.mjt.trading.stock.AmazonStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.GoogleStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.MicrosoftStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.StockPurchase;

import java.time.LocalDateTime;
//import java.util.Arrays;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Portfolio implements PortfolioAPI {
    StockPurchase[] stockPurchases;
    String owner;
    PriceChartAPI priceChart;
    double budget;
    int maxSize;

    int currSize;


    Portfolio(String owner, PriceChartAPI priceChart, double budget, int maxSize) {
        this.owner = owner;
        this.priceChart = priceChart;
        this.budget = budget;
        this.stockPurchases = new StockPurchase[maxSize];
        this.maxSize = maxSize;
    }

    Portfolio(String owner, PriceChartAPI priceChart, StockPurchase[] stockPurchases, double budget, int maxSize) {
        this.owner = owner;
        this.priceChart = priceChart;
        this.budget = budget;
//        this.stockPurchases = Arrays.copyOf(stockPurchases, maxSize);
        this.stockPurchases = new StockPurchase[maxSize];
        for (StockPurchase curr: stockPurchases) {
            this.stockPurchases[currSize++] = curr;
        }
        this.maxSize = maxSize;
    }

    @Override
    public StockPurchase buyStock(String stockTicker, int quantity) {
        if (quantity < 0) {
            return null;
        }

        if (stockPurchases.length + quantity > maxSize) {
            return null;
        }

        double currPrice = priceChart.getCurrentPrice(stockTicker);

        if (budget < currPrice) {
            return null;
        }

        budget -= currPrice;
        priceChart.changeStockPrice(stockTicker, 5);

        StockPurchase purchase = switch (stockTicker) {
            case AmazonStockPurchase.STOCK_TICKER -> new AmazonStockPurchase(quantity, LocalDateTime.now(), currPrice);
            case GoogleStockPurchase.STOCK_TICKER -> new GoogleStockPurchase(quantity, LocalDateTime.now(), currPrice);
            case MicrosoftStockPurchase.STOCK_TICKER -> new MicrosoftStockPurchase(quantity, LocalDateTime.now(), currPrice);
            default -> null;
        };

        stockPurchases[currSize++] = purchase;

        return purchase;
    }

    @Override
    public StockPurchase[] getAllPurchases() {
        return stockPurchases;
    }

    @Override
    public StockPurchase[] getAllPurchases(LocalDateTime startTimestamp, LocalDateTime endTimestamp) {
        StockPurchase[] filteredArray = new StockPurchase[maxSize];
        int filteredSize = 0;

        for (StockPurchase curr: stockPurchases) {
            LocalDateTime purchaseTimestamp = curr.getPurchaseTimestamp();
            if (purchaseTimestamp.isAfter(startTimestamp) && purchaseTimestamp.isBefore(endTimestamp)) {
                filteredArray[filteredSize++] = curr;
            }
        }

        return filteredArray;
    }

    @Override
    public double getNetWorth() {
        double sum = 0.0;

        for (StockPurchase curr: stockPurchases) {
            sum += curr.getTotalPurchasePrice();
        }

        return BigDecimal.valueOf(sum).setScale(2, RoundingMode.HALF_UP).doubleValue();
//        return sum;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public double getRemainingBudget() {
        return BigDecimal.valueOf(budget).setScale(2, RoundingMode.HALF_UP).doubleValue();
//        return budget;
    }

    public static void main(String[] args) {
        new Portfolio("Hi", new PriceChart(3, 3, 3), 13.2, 10);
        new AmazonStockPurchase(3, LocalDateTime.now(), 13.2);
    }

}
