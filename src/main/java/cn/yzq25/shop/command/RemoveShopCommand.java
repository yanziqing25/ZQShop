package cn.yzq25.shop.command;

import cn.nukkit.Player;
import cn.nukkit.command.*;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import cn.yzq25.shop.ShopMain;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Yanziqing25
 */
public class RemoveShopCommand extends PluginCommand<ShopMain> implements CommandExecutor {

    public RemoveShopCommand() {
        super("removeshop", ShopMain.getInstance());
        this.setExecutor(this);
        Map<String, CommandParameter[]> commandParameters = new LinkedHashMap<>();
        this.setCommandParameters(new HashMap<>());
        this.setCommandParameters(commandParameters);
        this.setAliases(new String[]{"rs", "移除商店"});
        this.setPermission("shop.command.removeshop");
        this.setDescription("移除一个商店");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(TextFormat.RED + "请在游戏中使用此命令!");
            return false;
        }
        if (args.length != 0) {
            return false;
        }
        Player player = (Player) sender;
        if (getPlugin().settingHandler.containsKey(player.getName())) {
            player.sendTip(TextFormat.RED + "你已处于设置模式下!如需中途取消请输入命令\"/cancelshop\"或\"/cs\"");
            return true;
        }
        if (getPlugin().removingHandler.containsKey(player.getName())) {
            player.sendTip(TextFormat.RED + "你已处于移除模式下!如需中途取消请输入命令\"/cancelshop\"或\"/cs\"");
            return true;
        }
        getPlugin().removingHandler.put(player.getName(), 1);
        player.sendTitle("请点击木牌完成商店的移除!");
        return true;
    }
}
