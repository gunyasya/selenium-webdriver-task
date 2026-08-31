package utils;

public class PriceParser {
    public static Double parsePrice(String price) {
        return Double.parseDouble(price.replaceAll("[^0-9.]", ""));
    }
}
