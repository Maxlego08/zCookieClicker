package fr.maxlego08.cookie.buttons;

import fr.maxlego08.cookie.CookieManager;
import fr.maxlego08.cookie.CookiePlayer;
import fr.maxlego08.cookie.CookiePlugin;
import fr.maxlego08.cookie.CookieUpgrade;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class CookieCheckButton extends AbstractCookieButton {

    public CookieCheckButton(CookiePlugin plugin, CookieUpgrade cookieUpgrade) {
        super(plugin, cookieUpgrade);
    }

    @Override
    public boolean hasPermission() {
        return true;
    }

    @Override
    public boolean checkPermission(Player player, InventoryDefault inventory, Placeholders placeholders) {
        if (this.cookieUpgrade == null) return false;

        CookieManager manager = this.plugin.getCookieManager();
        CookiePlayer cookiePlayer = manager.getCookiePlayer(player);

        long currentAmount = cookiePlayer.getUpgrade(this.cookieUpgrade).longValue();

        BigDecimal upgradePrice = manager.getUpgrade(this.cookieUpgrade).cost();
        return currentAmount == 0 && upgradePrice.compareTo(cookiePlayer.getCookie()) > 0;
    }
}
