package driver;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrowserContext {

    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();

    public static void setBrowser(Browser name) {
        BROWSER.set(name);
    }

    public static Browser getBrowser() {
        return Optional.ofNullable(BROWSER.get())
                .orElseThrow(() -> new IllegalStateException("Browser not set"));
    }

    public static void clear() {
        BROWSER.remove();
    }
}
