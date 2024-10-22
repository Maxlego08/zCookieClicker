package fr.maxlego08.cookie.command.commands;

import fr.maxlego08.cookie.CookiePlugin;
import fr.maxlego08.cookie.command.VCommand;
import fr.maxlego08.cookie.zcore.enums.Permission;
import fr.maxlego08.cookie.zcore.utils.commands.CommandType;

public class CommandCookieClicker extends VCommand {

    public CommandCookieClicker(CookiePlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZCOOKIECLICKER_USE);
        this.addSubCommand(new CommandCookieClickerReload(plugin));
		this.onlyPlayers();
    }

    @Override
    protected CommandType perform(CookiePlugin plugin) {

        plugin.getInventoryManager().openInventory(this.player, plugin, "cookies");

        return CommandType.SUCCESS;
    }

}
