package fr.maxlego08.cookie.loader;

import fr.maxlego08.cookie.CookiePlugin;
import fr.maxlego08.cookie.CookieUpgrade;
import fr.maxlego08.cookie.buttons.CookiePurchaseButton;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class CookiePurchaseLoader implements ButtonLoader {

    private final CookiePlugin plugin;

    public CookiePurchaseLoader(CookiePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Class<? extends Button> getButton() {
        return CookiePurchaseButton.class;
    }

    @Override
    public String getName() {
        return "ZCOOKIECLICKER_UPGRADE";
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {

        CookieUpgrade cookieUpgrade = CookieUpgrade.valueOf(configuration.getString(path + "upgrade", "").toUpperCase());

        return new CookiePurchaseButton(this.plugin, cookieUpgrade);
    }
}
