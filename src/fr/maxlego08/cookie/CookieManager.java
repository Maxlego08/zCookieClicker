package fr.maxlego08.cookie;

import fr.maxlego08.cookie.buttons.CookieButton;
import fr.maxlego08.cookie.dto.CookiePlayerDTO;
import fr.maxlego08.cookie.dto.CookieUpgradeDTO;
import fr.maxlego08.cookie.placeholder.LocalPlaceholder;
import fr.maxlego08.cookie.zcore.utils.ZUtils;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CookieManager extends ZUtils implements Listener {

    private final CookiePlugin plugin;
    private final Map<CookieUpgrade, UpgradeData> upgrades = new HashMap<>();
    private final Map<UUID, CookiePlayer> players = new HashMap<>();
    private BigDecimal purchasePercent;
    private String decimalFormat = "#,###.#";
    private boolean useShortNumberFormat;
    private String decimalFormatPattern;
    private List<String> suffixes;

    public CookieManager(CookiePlugin plugin) {
        this.plugin = plugin;

        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        placeholder.register("cookie", (player) -> formatNumber(getCookiePlayer(player).getCookie()));
        placeholder.register("cps", (player) -> formatNumber(getCookiePlayer(player).getCookiePerSeconds()));
        placeholder.register("upgrade_", (player, args) -> {
            try {
                CookieUpgrade upgrade = CookieUpgrade.valueOf(args.toUpperCase());
                return getCookiePlayer(player).getUpgrades().getOrDefault(upgrade, BigDecimal.ZERO).toString();
            } catch (Exception exception) {
                return "0";
            }
        });
    }

    public void loadConfiguration() {
        FileConfiguration configuration = this.plugin.getConfig();
        List<Map<?, ?>> upgrades = configuration.getMapList("upgrades");

        this.upgrades.clear();
        for (Map<?, ?> upgrade : upgrades) {
            TypedMapAccessor accessor = new TypedMapAccessor((Map<String, Object>) upgrade);
            String typeString = accessor.getString("type");
            String cost = accessor.getString("cost");
            String cps = accessor.getString("cps");

            CookieUpgrade type = CookieUpgrade.valueOf(typeString);
            this.upgrades.put(type, new UpgradeData(new BigDecimal(cost), new BigDecimal(cps)));
        }

        this.purchasePercent = BigDecimal.valueOf(1 + (configuration.getDouble("price-upgrade-percent") / 100));
        this.decimalFormat = configuration.getString("decimal-format", "#,###.#");

        this.useShortNumberFormat = configuration.getBoolean("short-number-format.enabled", true);
        this.decimalFormatPattern = configuration.getString("short-number-format.decimal-format", "#.##");
        this.suffixes = configuration.getStringList("short-number-format.suffixes");
    }

    public void startTask() {
        var scheduler = this.plugin.getInventoryManager().getScheduler();
        scheduler.runTaskTimerAsynchronously(20, 20, () -> {

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (this.players.containsKey(onlinePlayer.getUniqueId())) {
                    var cookiePlayer = getCookiePlayer(onlinePlayer);
                    var cps = cookiePlayer.getCookiePerSeconds();
                    this.addCookie(onlinePlayer, cps);
                }
            }
        });
    }

    public void loadInventories() {

        var inventoryManager = this.plugin.getInventoryManager();
        inventoryManager.deleteInventories(this.plugin);

        File folder = new File(plugin.getDataFolder(), "inventories");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        var files = List.of("cookies", "cookies-upgrade");
        for (String file : files) {
            if (!new File(folder, file + ".yml").exists()) {
                this.plugin.saveResource("inventories/" + file + ".yml", false);
            }
        }

        files(folder, file -> {
            try {
                inventoryManager.loadInventory(this.plugin, file);
            } catch (InventoryException exception) {
                exception.printStackTrace();
            }
        });
    }

    public CookiePlayer getCookiePlayer(Player player) {
        return this.players.computeIfAbsent(player.getUniqueId(), e -> new CookiePlayer(plugin));
    }

    public Map<CookieUpgrade, UpgradeData> getUpgrades() {
        return upgrades;
    }

    public void addCookie(Player player, BigDecimal decimal) {

        CookiePlayer cookiePlayer = getCookiePlayer(player);
        cookiePlayer.add(decimal);

        this.updateInventory(player);
    }

    public void updateInventory(Player player) {
        Inventory topInventory = player.getOpenInventory().getTopInventory();
        if (topInventory.getHolder() instanceof InventoryDefault inventoryDefault) {
            var spigotInventory = inventoryDefault.getSpigotInventory();
            for (Button button : inventoryDefault.getButtons()) {
                if (button instanceof CookieButton) {
                    spigotInventory.setItem(button.getSlot(), button.getCustomItemStack(player));
                }
            }
        }
    }

    public BigDecimal calculatePrice(CookieUpgrade cookieUpgrade, BigDecimal upgradeAmount) {

        BigDecimal price = this.upgrades.getOrDefault(cookieUpgrade, new UpgradeData(BigDecimal.ONE, BigDecimal.ZERO)).cost();

        if (upgradeAmount.longValue() < 1) {
            return price;
        }

        for (long i = 0; i < upgradeAmount.longValue(); i++) {
            price = price.multiply(this.purchasePercent);
        }

        return price;
    }

    public DecimalFormat getDecimalFormat() {
        return new DecimalFormat(this.decimalFormat);
    }

    public void loadPlayer(CookiePlayerDTO dto, List<CookieUpgradeDTO> upgradeDTOS) {

        CookiePlayer cookiePlayer = new CookiePlayer(this.plugin);
        cookiePlayer.setCookie(dto.cookie());

        for (CookieUpgradeDTO upgradeDTO : upgradeDTOS) {
            cookiePlayer.getUpgrades().put(upgradeDTO.upgrade(), upgradeDTO.amount());
        }

        this.players.put(dto.unique_id(), cookiePlayer);
    }

    public CookiePlayer removeCookiePlayer(Player player) {
        return this.players.remove(player.getUniqueId());
    }

    public String formatNumber(BigDecimal value) {
        if (!useShortNumberFormat) {
            return getDecimalFormat().format(value);
        }

        int suffixIndex = 0;
        BigDecimal scaledValue = value;

        if (scaledValue.compareTo(new BigDecimal("1000")) <= 0) {
            return getDecimalFormat().format(value);
        }

        while (scaledValue.compareTo(new BigDecimal("1000")) >= 0 && suffixIndex < suffixes.size() - 1) {
            scaledValue = scaledValue.divide(new BigDecimal("1000"), 2, RoundingMode.HALF_DOWN);
            suffixIndex++;
        }

        DecimalFormat currentFormat = new DecimalFormat(decimalFormatPattern);
        return currentFormat.format(scaledValue) + suffixes.get(suffixIndex);
    }
}
