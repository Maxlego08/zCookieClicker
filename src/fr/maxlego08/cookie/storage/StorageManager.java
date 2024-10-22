package fr.maxlego08.cookie.storage;

import fr.maxlego08.cookie.CookiePlayer;
import fr.maxlego08.cookie.CookiePlugin;
import fr.maxlego08.cookie.CookieUpgrade;
import fr.maxlego08.cookie.dto.CookiePlayerDTO;
import fr.maxlego08.cookie.dto.CookieUpgradeDTO;
import fr.maxlego08.cookie.migrations.CookiePlayerMigration;
import fr.maxlego08.cookie.migrations.CookieUpgradeMigration;
import fr.maxlego08.sarah.DatabaseConfiguration;
import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.sarah.HikariDatabaseConnection;
import fr.maxlego08.sarah.MigrationManager;
import fr.maxlego08.sarah.MySqlConnection;
import fr.maxlego08.sarah.RequestHelper;
import fr.maxlego08.sarah.SqliteConnection;
import fr.maxlego08.sarah.database.DatabaseType;
import fr.maxlego08.sarah.logger.JULogger;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.math.BigDecimal;
import java.util.UUID;

public class StorageManager implements Listener {

    private final CookiePlugin plugin;
    private DatabaseConnection databaseConnection;
    private RequestHelper requestHelper;

    public StorageManager(CookiePlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {

        this.plugin.getLogger().info("Load SQL...");

        MigrationManager.setMigrationTableName("zcookieclicker_migrations");
        MigrationManager.registerMigration(new CookiePlayerMigration());
        MigrationManager.registerMigration(new CookieUpgradeMigration());

        var config = this.plugin.getConfig();
        String type = config.getString("sql.type", "SQLITE").toLowerCase();
        String user = config.getString("sql.user");
        String password = config.getString("sql.password");
        String host = config.getString("sql.host");
        String dataBase = config.getString("sql.database");
        String prefix = config.getString("sql.prefix");
        int port = config.getInt("sql.port");
        boolean enableDebug = config.getBoolean("sql.debug", false);

        this.databaseConnection = switch (type) {
            case "sqlite" ->
                    new SqliteConnection(new DatabaseConfiguration(prefix, user, password, port, host, dataBase, enableDebug, DatabaseType.SQLITE), plugin.getDataFolder());
            case "hikari" ->
                    new HikariDatabaseConnection(new DatabaseConfiguration(prefix, user, password, port, host, dataBase, enableDebug, DatabaseType.MYSQL));
            default ->
                    new MySqlConnection(new DatabaseConfiguration(prefix, user, password, port, host, dataBase, enableDebug, DatabaseType.MYSQL));
        };

        fr.maxlego08.sarah.logger.Logger logger = JULogger.from(plugin.getLogger());
        this.requestHelper = new RequestHelper(this.databaseConnection, logger);

        if (!this.databaseConnection.isValid()) {
            this.plugin.getLogger().severe("Unable to connect to database !");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        } else {
            this.plugin.getLogger().info("The database connection is valid ! (" + this.databaseConnection.getDatabaseConfiguration().getHost() + ")");
        }

        MigrationManager.execute(this.databaseConnection, logger);
    }

    @EventHandler
    public void onConnect(PlayerJoinEvent event) {
        var scheduler = this.plugin.getInventoryManager().getScheduler();
        scheduler.runTaskAsynchronously(() -> {

            var cookieDTOS = this.requestHelper.select("%prefix%players", CookiePlayerDTO.class, table -> table.where("unique_id", event.getPlayer().getUniqueId()));
            if (cookieDTOS.isEmpty()) return;

            var dto = cookieDTOS.get(0);
            var upgradeDTOS = this.requestHelper.select("%prefix%upgrades", CookieUpgradeDTO.class, table -> table.where("unique_id", event.getPlayer().getUniqueId()));

            this.plugin.getCookieManager().loadPlayer(dto, upgradeDTOS);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        var scheduler = this.plugin.getInventoryManager().getScheduler();
        scheduler.runTaskAsynchronously(() -> {
            var player = event.getPlayer();
            CookiePlayer cookiePlayer = this.plugin.getCookieManager().removeCookiePlayer(player);
            if (cookiePlayer == null) return;

            this.requestHelper.upsert("%prefix%players", table -> {
                table.uuid("unique_id", player.getUniqueId()).primary();
                table.decimal("cookie", cookiePlayer.getCookie());
            });
        });
    }

    public void upsertUpgrade(UUID uuid, CookieUpgrade cookieUpgrade, BigDecimal amount) {
        var scheduler = this.plugin.getInventoryManager().getScheduler();
        scheduler.runTaskAsynchronously(() -> this.requestHelper.upsert("%prefix%upgrades", table -> {
            table.uuid("unique_id", uuid).primary();
            table.string("upgrade", cookieUpgrade.name()).primary();
            table.decimal("amount", amount);
        }));
    }
}
