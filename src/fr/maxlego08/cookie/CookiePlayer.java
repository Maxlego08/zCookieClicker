package fr.maxlego08.cookie;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CookiePlayer {

    private final CookiePlugin plugin;
    private final Map<CookieUpgrade, BigDecimal> upgrades = new HashMap<>();
    private BigDecimal cookie = BigDecimal.ZERO;
    private BigDecimal totalCookie = BigDecimal.ZERO;

    public CookiePlayer(CookiePlugin plugin) {
        this.plugin = plugin;
    }

    public void add(BigDecimal value) {
        this.cookie = this.cookie.add(value);
        this.totalCookie = this.totalCookie.add(value);
    }

    public void add(long value) {
        this.add(BigDecimal.valueOf(value));
    }

    public void add(double value) {
        this.add(BigDecimal.valueOf(value));
    }

    public BigDecimal getCookie() {
        return cookie;
    }

    public void setCookie(BigDecimal cookie) {
        this.cookie = cookie;
    }

    public void setTotalCookie(BigDecimal totalCookie) {
        this.totalCookie = totalCookie;
    }

    public Map<CookieUpgrade, BigDecimal> getUpgrades() {
        return upgrades;
    }

    public BigDecimal getUpgrade(CookieUpgrade cookieUpgrade) {
        return upgrades.getOrDefault(cookieUpgrade, BigDecimal.ZERO);
    }

    public BigDecimal getCookiePerSeconds() {
        var cookieUpgrades = this.plugin.getCookieManager().getUpgrades();
        AtomicReference<BigDecimal> totalCPS = new AtomicReference<>(BigDecimal.ZERO);
        this.upgrades.forEach((upgrade, amount) -> {
            if (cookieUpgrades.containsKey(upgrade)) {
                var cookieUpgrade = cookieUpgrades.get(upgrade);
                var currentCPS = cookieUpgrade.cps().multiply(amount);
                totalCPS.set(totalCPS.get().add(currentCPS));
            }
        });
        return totalCPS.get();
    }

    public void remove(BigDecimal value) {
        this.cookie = this.cookie.subtract(value);
    }

    public void add(CookieUpgrade cookieUpgrade, int amount) {
        this.upgrades.put(cookieUpgrade, this.upgrades.getOrDefault(cookieUpgrade, BigDecimal.ZERO).add(BigDecimal.valueOf(amount)));
    }

    public BigDecimal getTotalCookie() {
        return totalCookie;
    }
}
