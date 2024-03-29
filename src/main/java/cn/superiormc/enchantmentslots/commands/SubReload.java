package cn.superiormc.enchantmentslots.commands;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.configs.Messages;
import cn.superiormc.enchantmentslots.methods.ExtraSlotsItem;
import org.bukkit.command.CommandSender;

public class SubReload {

    public static void SubReloadCommand(CommandSender sender) {
        if (sender.hasPermission("enchantmentslots.admin")) {
            EnchantmentSlots.instance.reloadConfig();
            Messages.init();
            ExtraSlotsItem.init();
            sender.sendMessage(Messages.getMessages("plugin-reloaded"));
        }
        else {
            sender.sendMessage(Messages.getMessages("error-miss-permission"));
        }
    }
}
