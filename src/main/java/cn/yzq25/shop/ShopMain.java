package cn.yzq25.shop;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import cn.yzq25.shop.command.*;
import cn.yzq25.utils.ZQUtils;
import money.MoneyAPI;

import java.io.File;
import java.util.*;

/**
 * Created by Yanziqing25
 */
public class ShopMain extends PluginBase {
    private static ShopMain instance;
    public MoneyAPI money;
    public String publicShopDataFolder;
    public String privateShopDataFolder;
    public String recycleShopDataFolder;
    private List<Shop> shopsList;
    public Map<String, Map<String, Object>> settingHandler;
    public Map<String, Integer> removingHandler;
    public Map<Player, Shop> buyers;

    @Override
    public void onLoad() {
        instance = this;
        this.money = MoneyAPI.getInstance();
        saveDefaultConfig();
     }

    @Override
    public void onEnable() {
        if (getConfig().getBoolean("check_update", true)) {
            ZQUtils.checkPluginUpdate(this);
        }
        this.publicShopDataFolder = getDataFolder().getPath() + "/public_shops/";
        this.privateShopDataFolder = getDataFolder().getPath() + "/private_shops/";
        this.recycleShopDataFolder = getDataFolder().getPath() + "/recycle_shops/";
        new File(publicShopDataFolder).mkdirs();
        new File(privateShopDataFolder).mkdirs();
        new File(recycleShopDataFolder).mkdirs();
        this.shopsList = new ArrayList<>();
        this.settingHandler = new HashMap<>();
        this.removingHandler = new HashMap<>();
        this.buyers = new HashMap<>();
        getServer().getPluginManager().registerEvents(new ShopEventListener(), this);
        getServer().getCommandMap().register("shop", new AddPublicShopCommand(), "addpublicshop");
        getServer().getCommandMap().register("shop", new AddPrivateShopCommand(), "addprivateshop");
        getServer().getCommandMap().register("shop", new AddRecycleShopCommand(), "addrecycleshop");
        getServer().getCommandMap().register("shop", new RemoveShopCommand(), "removeshop");
        getServer().getCommandMap().register("shop", new CancelShopCommand(), "cancelshop");
        loadShops();
    }

    @Override
    public void onDisable() {
        getLogger().info(TextFormat.RED + "插件已关闭!");
    }

    public static ShopMain getInstance() {
        return instance;
    }

    public Shop getShop(Block block) {
        if (block.getId() == 63 || block.getId() == 68 || block.getId() == 323) {
            for (Shop shop : shopsList) {
                if (shop.getPosition().equals(block)) {
                    return shop;
                }
            }
        }
        return null;
    }

    private void loadShops() {
        File[] pbfs = new File(publicShopDataFolder).listFiles();
        for (File file : pbfs) {
            String file_name = file.getName();
            this.loadPublicShop(Integer.valueOf(file_name.substring(0, file_name.lastIndexOf("."))));
        }

        File[] pvfs = new File(privateShopDataFolder).listFiles();
        for (File file : pvfs) {
            String file_name = file.getName();
            this.loadPrivateShop(Integer.valueOf(file_name.substring(0, file_name.lastIndexOf("."))));
        }

        File[] rfs = new File(recycleShopDataFolder).listFiles();
        for (File file : rfs) {
            String file_name = file.getName();
            this.loadRecycleShop(Integer.valueOf(file_name.substring(0, file_name.lastIndexOf("."))));
        }
    }




    public Config getPublicShopConfig(int id) {
        return new Config(this.publicShopDataFolder + id + ".yml", Config.YAML);
    }

    public synchronized void addPublicShop(int id, Position position, String itemID, int price, int count) {
        Config publicShopConfig = getPublicShopConfig(id);
        publicShopConfig.set("x", position.getFloorX());
        publicShopConfig.set("y", position.getFloorY());
        publicShopConfig.set("z", position.getFloorZ());
        publicShopConfig.set("world", position.getLevel().getName());
        publicShopConfig.set("itemID", itemID);
        publicShopConfig.set("price", price);
        publicShopConfig.set("count", count);
        publicShopConfig.save();
        loadPublicShop(id);
    }

    public synchronized void removePublicShop(int id, Shop shop) {
        File[] fs = new File(publicShopDataFolder).listFiles();
        for (File file : fs) {
            String file_name = file.getName();
            if (Integer.valueOf(file_name.substring(0, file_name.lastIndexOf("."))) == id) {
                shopsList.remove(shop);
                file.delete();
            }
        }
    }

    private synchronized void loadPublicShop(int id) {
        Map<String, Object> shopInfo = getPublicShopConfig(id).getAll();
        if (shopInfo == null) return;
        int x = (int) shopInfo.get("x");
        int y = (int) shopInfo.get("y");
        int z = (int) shopInfo.get("z");
        String world = (String) shopInfo.get("world");
        int itemID = ZQUtils.getItemID((String) shopInfo.get("itemID"));
        int meta = ZQUtils.getItemMeta((String) shopInfo.get("itemID"));
        int price = (int) shopInfo.get("price");
        int count = (int) shopInfo.get("count");
        shopsList.add(new PublicShop(id, x, y, z, world, itemID, meta, price, count));
    }



    public Config getPrivateShopConfig(int id) {
        return new Config(this.privateShopDataFolder + id + ".yml", Config.YAML);
    }

    public synchronized void addPrivateShop(int id, Position position, String itemID, int price, String owner, int count) {
        Config privateShopConfig = getPrivateShopConfig(id);
        privateShopConfig.set("x", position.getFloorX());
        privateShopConfig.set("y", position.getFloorY());
        privateShopConfig.set("z", position.getFloorZ());
        privateShopConfig.set("world", position.getLevel().getName());
        privateShopConfig.set("itemID", itemID);
        privateShopConfig.set("price", price);
        privateShopConfig.set("owner", owner);
        privateShopConfig.set("count", count);
        privateShopConfig.save();
        loadPrivateShop(id);
    }

    public synchronized void removePrivateShop(int id, Shop shop) {
        File[] fs = new File(privateShopDataFolder).listFiles();
        for (File file : fs) {
            String file_name = file.getName();
            if (Integer.valueOf(file_name.substring(0, file_name.lastIndexOf("."))) == id) {
                shopsList.remove(shop);
                file.delete();
            }
        }
    }

    private synchronized void loadPrivateShop(int id) {
        Map<String, Object> shopInfo = getPrivateShopConfig(id).getAll();
        if (shopInfo == null) return;
        int x = (int) shopInfo.get("x");
        int y = (int) shopInfo.get("y");
        int z = (int) shopInfo.get("z");
        String world = (String) shopInfo.get("world");
        int itemID = ZQUtils.getItemID((String) shopInfo.get("itemID"));
        int meta = ZQUtils.getItemMeta((String) shopInfo.get("itemID"));
        int price = (int) shopInfo.get("price");
        String owner = (String) shopInfo.get("owner");
        int count = (int) shopInfo.get("count");
        shopsList.add(new PrivateShop(id, x, y, z, world, itemID, meta, price, owner, count));
    }



    public Config getRecycleShopConfig(int id) {
        return new Config(this.recycleShopDataFolder + id + ".yml", Config.YAML);
    }

    public synchronized void addRecycleShop(int id, Position position, String itemID, int price, int count) {
        Config recycleShopConfig = getRecycleShopConfig(id);
        recycleShopConfig.set("x", position.getFloorX());
        recycleShopConfig.set("y", position.getFloorY());
        recycleShopConfig.set("z", position.getFloorZ());
        recycleShopConfig.set("world", position.getLevel().getName());
        recycleShopConfig.set("itemID", itemID);
        recycleShopConfig.set("price", price);
        recycleShopConfig.set("count", count);
        recycleShopConfig.save();
        loadRecycleShop(id);
    }

    public synchronized void removeRecycleShop(int id, Shop shop) {
        File[] fs = new File(recycleShopDataFolder).listFiles();
        for (File file : fs) {
            String file_name = file.getName();
            if (Integer.valueOf(file_name.substring(0, file_name.lastIndexOf("."))) == id) {
                shopsList.remove(shop);
                file.delete();
            }
        }
    }

    private synchronized void loadRecycleShop(int id) {
        Map<String, Object> shopInfo = getRecycleShopConfig(id).getAll();
        if (shopInfo == null) return;
        int x = (int) shopInfo.get("x");
        int y = (int) shopInfo.get("y");
        int z = (int) shopInfo.get("z");
        String world = (String) shopInfo.get("world");
        int itemID = ZQUtils.getItemID((String) shopInfo.get("itemID"));
        int meta = ZQUtils.getItemMeta((String) shopInfo.get("itemID"));
        int price = (int) shopInfo.get("price");
        int count = (int) shopInfo.get("count");
        shopsList.add(new RecycleShop(id, x, y, z, world, itemID, meta, price, count));
    }
}
