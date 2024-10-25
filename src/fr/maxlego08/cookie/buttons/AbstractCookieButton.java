package fr.maxlego08.cookie.buttons;

import fr.maxlego08.cookie.CookieManager;
import fr.maxlego08.cookie.CookiePlayer;
import fr.maxlego08.cookie.CookiePlugin;
import fr.maxlego08.cookie.CookieUpgrade;
import fr.maxlego08.cookie.UpgradeData;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.button.ZButton;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

public abstract class AbstractCookieButton extends ZButton {

    protected final CookiePlugin plugin;
    protected final CookieUpgrade cookieUpgrade;

    protected AbstractCookieButton(CookiePlugin plugin, CookieUpgrade cookieUpgrade) {
        this.plugin = plugin;
        this.cookieUpgrade = cookieUpgrade;
    }

    @Override
    public ItemStack getCustomItemStack(Player player) {

        CookieManager manager = this.plugin.getCookieManager();

        CookiePlayer cookiePlayer = manager.getCookiePlayer(player);
        BigDecimal upgradeAmount = cookiePlayer.getUpgrades().getOrDefault(this.cookieUpgrade, BigDecimal.ZERO);

        BigDecimal price = manager.calculatePrice(this.cookieUpgrade, upgradeAmount);

        var upgradeCPS = manager.getUpgrades().getOrDefault(cookieUpgrade, new UpgradeData(BigDecimal.ONE, BigDecimal.ZERO)).cps();
        BigDecimal cps = upgradeCPS.multiply(upgradeAmount);

        Placeholders placeholders = new Placeholders();

        placeholders.register("price", manager.formatNumber(price));
        placeholders.register("amount", manager.formatNumber(upgradeAmount));
        placeholders.register("cps", manager.formatNumber(cps));

        long size = upgradeAmount.longValue();
        placeholders.register("amount-integer", String.valueOf(size <= 0 ? 1 : size));

        return getItemStack().build(player, false, placeholders);
    }

}
