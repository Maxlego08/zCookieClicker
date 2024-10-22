package fr.maxlego08.cookie.migrations;

import fr.maxlego08.sarah.database.Migration;

public class CookieUpgradeMigration extends Migration {

    @Override
    public void up() {
        create("%prefix%upgrades", table -> {
            table.uuid("unique_id").primary();
            table.string("upgrade", 255).primary();
            table.decimal("amount", 65, 0);
        });
    }
}
