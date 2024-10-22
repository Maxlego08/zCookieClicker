package fr.maxlego08.cookie.buttons;

import fr.maxlego08.cookie.CookiePlayer;
import fr.maxlego08.cookie.CookiePlugin;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class CookieButton extends ZButton {

    private final CookiePlugin plugin;

    public CookieButton(Plugin plugin) {
        this.plugin = (CookiePlugin) plugin;
    }

    @Override
    public ItemStack getCustomItemStack(Player player) {

        var manager = this.plugin.getCookieManager();
        Placeholders placeholders = new Placeholders();

        CookiePlayer cookiePlayer = manager.getCookiePlayer(player);
        BigDecimal cookie = cookiePlayer.getCookie();
        BigDecimal cookiePerSeconds = cookiePlayer.getCookiePerSeconds();

        placeholders.register("cookie-per-seconds", manager.formatNumber(cookiePerSeconds));
        placeholders.register("cookie", manager.formatNumber(cookie));

        return getItemStack().build(player, false, placeholders);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot, Placeholders placeholders) {
        if (event.getClick().isLeftClick() || event.getClick().isRightClick()) {
            super.onClick(player, event, inventory, slot, placeholders);

            this.plugin.getCookieManager().addCookie(player, BigDecimal.ONE);
        }
    }
}
