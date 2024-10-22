package fr.maxlego08.cookie.buttons;

import fr.maxlego08.cookie.CookieManager;
import fr.maxlego08.cookie.CookiePlayer;
import fr.maxlego08.cookie.CookiePlugin;
import fr.maxlego08.cookie.CookieUpgrade;
import fr.maxlego08.cookie.UpgradeData;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class CookiePurchaseButton extends ZButton {

    private final CookiePlugin plugin;
    private final CookieUpgrade cookieUpgrade;

    public CookiePurchaseButton(CookiePlugin plugin, CookieUpgrade cookieUpgrade) {
        this.plugin = plugin;
        this.cookieUpgrade = cookieUpgrade;
    }

    @Override
    public ItemStack getCustomItemStack(Player player) {

        CookieManager manager = this.plugin.getCookieManager();

        CookiePlayer cookiePlayer = manager.getCookiePlayer(player);
        BigDecimal upgradeAmount = cookiePlayer.getUpgrades().getOrDefault(this.cookieUpgrade, BigDecimal.ZERO);

        BigDecimal price = manager.calculatePrice(this.cookieUpgrade, upgradeAmount);
        DecimalFormat decimalFormat = manager.getDecimalFormat();

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

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot, Placeholders placeholders) {

        CookieManager cookieManager = this.plugin.getCookieManager();

        CookiePlayer cookiePlayer = cookieManager.getCookiePlayer(player);
        BigDecimal upgradeAmount = cookiePlayer.getUpgrades().getOrDefault(this.cookieUpgrade, BigDecimal.ZERO);

        BigDecimal price = cookieManager.calculatePrice(this.cookieUpgrade, upgradeAmount);
        BigDecimal cookie = cookiePlayer.getCookie();

        if (cookie.compareTo(price) > 0) {
            super.onClick(player, event, inventory, slot, placeholders);
            cookiePlayer.remove(price);
            cookiePlayer.add(this.cookieUpgrade, 1);
            this.plugin.getInventoryManager().updateInventory(player);

            this.plugin.getStorageManager().upsertUpgrade(player.getUniqueId(), this.cookieUpgrade, cookiePlayer.getUpgrades().get(this.cookieUpgrade));
        }
    }
}
