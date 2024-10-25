package fr.maxlego08.cookie.buttons;

import fr.maxlego08.cookie.CookieManager;
import fr.maxlego08.cookie.CookiePlayer;
import fr.maxlego08.cookie.CookiePlugin;
import fr.maxlego08.cookie.CookieUpgrade;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.math.BigDecimal;

public class CookiePurchaseButton extends AbstractCookieButton {

    public CookiePurchaseButton(CookiePlugin plugin, CookieUpgrade cookieUpgrade) {
        super(plugin, cookieUpgrade);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot, Placeholders placeholders) {

        CookieManager cookieManager = this.plugin.getCookieManager();

        CookiePlayer cookiePlayer = cookieManager.getCookiePlayer(player);
        BigDecimal upgradeAmount = cookiePlayer.getUpgrades().getOrDefault(this.cookieUpgrade, BigDecimal.ZERO);

        BigDecimal price = cookieManager.calculatePrice(this.cookieUpgrade, upgradeAmount);
        BigDecimal cookie = cookiePlayer.getCookie();

        if (cookie.compareTo(price) >= 0) {
            super.onClick(player, event, inventory, slot, placeholders);
            cookiePlayer.remove(price);
            cookiePlayer.add(this.cookieUpgrade, 1);
            this.plugin.getInventoryManager().updateInventory(player);

            this.plugin.getStorageManager().upsertUpgrade(player.getUniqueId(), this.cookieUpgrade, cookiePlayer.getUpgrades().get(this.cookieUpgrade));
        }
    }

    @Override
    public boolean hasPermission() {
        return true;
    }

    @Override
    public boolean checkPermission(Player player, InventoryDefault inventory, Placeholders placeholders) {
        CookieManager cookieManager = this.plugin.getCookieManager();

        CookiePlayer cookiePlayer = cookieManager.getCookiePlayer(player);
        BigDecimal upgradeAmount = cookiePlayer.getUpgrades().getOrDefault(this.cookieUpgrade, BigDecimal.ZERO);
        BigDecimal price = cookieManager.calculatePrice(this.cookieUpgrade, upgradeAmount);
        BigDecimal cookie = cookiePlayer.getCookie();
        return cookie.compareTo(price) >= 0;
    }
}
