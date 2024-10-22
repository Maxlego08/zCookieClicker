package fr.maxlego08.cookie;

import fr.maxlego08.cookie.buttons.CookieButton;
import fr.maxlego08.cookie.command.commands.CommandCookieClicker;
import fr.maxlego08.cookie.loader.CookiePurchaseLoader;
import fr.maxlego08.cookie.placeholder.LocalPlaceholder;
import fr.maxlego08.cookie.save.Config;
import fr.maxlego08.cookie.save.MessageLoader;
import fr.maxlego08.cookie.storage.StorageManager;
import fr.maxlego08.cookie.zcore.ZPlugin;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.button.loader.NoneLoader;

/**
 * System to create your plugins very simply Projet:
 * <a href="https://github.com/Maxlego08/TemplatePlugin">https://github.com/Maxlego08/TemplatePlugin</a>
 *
 * @author Maxlego08
 */
public class CookiePlugin extends ZPlugin {

    private final StorageManager storageManager = new StorageManager(this);
    private CookieManager cookieManager;
    private InventoryManager inventoryManager;
    private ButtonManager buttonManager;

    @Override
    public void onEnable() {

        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        placeholder.setPrefix("zcookiecliker");

        this.preEnable();

        this.inventoryManager = getProvider(InventoryManager.class);
        this.buttonManager = getProvider(ButtonManager.class);

        this.saveDefaultConfig();
        this.cookieManager = new CookieManager(this);
        this.cookieManager.loadConfiguration();

        this.loadButtons();
        this.cookieManager.loadInventories();

        this.registerCommand("zcookiecliker", new CommandCookieClicker(this), "cookiecliker", "cookie");
        this.addListener(this.storageManager);

        this.addSave(Config.getInstance());
        this.addSave(new MessageLoader(this));

        this.loadFiles();

        this.storageManager.load();

        this.cookieManager.startTask();

        this.postEnable();
    }

    @Override
    public void onDisable() {

        this.preDisable();

        this.saveFiles();

        this.postDisable();
    }

    @Override
    public void reloadFiles() {
        super.reloadFiles();
        this.reloadConfig();
        this.cookieManager.loadConfiguration();
        this.cookieManager.loadInventories();
    }

    private void loadButtons() {
        this.buttonManager.register(new NoneLoader(this, CookieButton.class, "ZCOOKIECLICKER_COOKIE"));
        this.buttonManager.register(new CookiePurchaseLoader(this));
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public ButtonManager getButtonManager() {
        return buttonManager;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }
}
