package cn.yzq25.shop.command;

import cn.nukkit.Player;
import cn.nukkit.command.*;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import cn.yzq25.shop.ShopMain;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Yanziqing25
 */
public class AddPrivateShopCommand extends PluginCommand<ShopMain> implements CommandExecutor {

    public AddPrivateShopCommand() {
        super("addprivateshop", ShopMain.getInstance());
        this.setExecutor(this);
        this.setCommandParameters(new LinkedHashMap<String, CommandParameter[]>(){{put("default", new CommandParameter[]{new CommandParameter("物品ID", CommandParamType.STRING, false), new CommandParameter("价格(E币)", CommandParamType.INT, false), new CommandParameter("数量", CommandParamType.INT, false)});}});
        this.setAliases(new String[]{"apvs", "添加私人商店"});
        this.setPermission("shop.command.addprivateshop");
        this.setDescription("私人商店设置");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(TextFormat.RED + "请在游戏中使用此命令!");
            return false;
        }
        if (args.length != 3) {
            return false;
        }
        Player player = (Player) sender;
        if (getPlugin().settingHandler.containsKey(player.getName())) {
            player.sendTip(TextFormat.RED + "你已处于设置模式下!如需中途取消请输入命令\"/cancelshop\"或\"/cs\"");
            return true;
        }
        int id = new File(getPlugin().privateShopDataFolder).listFiles().length + 1;
        while (!getPlugin().getPrivateShopConfig(id).getAll().isEmpty()) {
            id++;
        }
        Map<String, Object> aarrggss = new HashMap();
        aarrggss.put("step", 2);
        aarrggss.put("id", id);
        aarrggss.put("itemID", args[0]);
        aarrggss.put("price", Integer.valueOf(args[1]));
        aarrggss.put("owner", player.getName());
        aarrggss.put("count", Integer.valueOf(args[2]));
        getPlugin().settingHandler.put(player.getName(), aarrggss);
        player.sendTitle("请点击木牌完成私人商店的创建!");
        return true;
    }
}
