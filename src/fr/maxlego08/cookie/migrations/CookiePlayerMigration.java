package fr.maxlego08.cookie.migrations;

import fr.maxlego08.sarah.database.Migration;

public class CookiePlayerMigration extends Migration {

    @Override
    public void up() {
        create("%prefix%players", table -> {
            table.uuid("unique_id").primary();
            table.decimal("cookie", 65, 5);
        });
    }
}
