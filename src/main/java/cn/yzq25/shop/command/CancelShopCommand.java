package cn.yzq25.shop.command;

import cn.nukkit.Player;
import cn.nukkit.command.*;
import cn.nukkit.utils.TextFormat;
import cn.yzq25.shop.ShopMain;

import java.util.HashMap;

/**
 * Created by Yanziqing25
 */
public class CancelShopCommand extends PluginCommand<ShopMain> implements CommandExecutor {

    public CancelShopCommand() {
        super("cancelshop", ShopMain.getInstance());
        this.setExecutor(this);
        this.setCommandParameters(new HashMap<>());
        this.setAliases(new String[]{"cs", "取消设置商店"});
        this.setPermission("shop.command.cancelshop");
        this.setDescription("取消设置商店");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(TextFormat.RED + "请在游戏中使用此命令!");
            return false;
        }
        Player player = (Player) sender;
        if (args.length != 0) {
            return false;
        }
        if (!getPlugin().settingHandler.containsKey(player.getName()) && !getPlugin().removingHandler.containsKey(player.getName())) {
            player.sendTitle(TextFormat.RED + "你不在设置模式中,无需取消!");
            return true;
        }
        getPlugin().settingHandler.remove(player.getName());
        getPlugin().removingHandler.remove(player.getName());
        player.sendTitle(TextFormat.GREEN + "取消设置模式成功!");
        return true;
    }
}
