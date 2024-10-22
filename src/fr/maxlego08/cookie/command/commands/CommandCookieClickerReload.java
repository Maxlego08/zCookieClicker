package fr.maxlego08.cookie.command.commands;

import fr.maxlego08.cookie.CookiePlugin;
import fr.maxlego08.cookie.command.VCommand;
import fr.maxlego08.cookie.zcore.enums.Message;
import fr.maxlego08.cookie.zcore.enums.Permission;
import fr.maxlego08.cookie.zcore.utils.commands.CommandType;

public class CommandCookieClickerReload extends VCommand {

	public CommandCookieClickerReload(CookiePlugin plugin) {
		super(plugin);
		this.setPermission(Permission.ZCOOKIECLICKER_RELOAD);
		this.addSubCommand("reload", "rl");
		this.setDescription(Message.DESCRIPTION_RELOAD);
	}

	@Override
	protected CommandType perform(CookiePlugin plugin) {
		
		plugin.reloadFiles();
		message(sender, Message.RELOAD);
		
		return CommandType.SUCCESS;
	}

}
