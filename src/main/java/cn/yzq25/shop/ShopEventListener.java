package cn.yzq25.shop;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import cn.yzq25.utils.ZQUtils;
import money.CurrencyType;

import java.util.Map;

/**
 * Created by Yanziqing25
 */
public class ShopEventListener implements Listener {

    private ShopMain mainclass;

    public ShopEventListener() {
        this.mainclass = ShopMain.getInstance();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onSettingShop(PlayerInteractEvent event) {
        if(event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_AIR || event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) return;
        Player player = event.getPlayer();
        if (mainclass.settingHandler.containsKey(player.getName())) {
            Block block = event.getBlock();
            if (block.getId() == 63 || block.getId() == 68 || block.getId() == 323) {
                if (mainclass.getShop(block) == null) {
                    switch ((int) mainclass.settingHandler.get(player.getName()).get("step")) {
                        case 1:
                            mainclass.addPublicShop((int) mainclass.settingHandler.get(player.getName()).get("id"), block.getLocation(), (String) mainclass.settingHandler.get(player.getName()).get("itemID"), (int) mainclass.settingHandler.get(player.getName()).get("price"), (int) mainclass.settingHandler.get(player.getName()).get("count"));
                            PublicShop shop = (PublicShop) mainclass.getShop(block);
                            ((BlockEntitySign) block.getLevel().getBlockEntity(block)).setText("§4官方商品", ZQUtils.getItemNameInChinese(shop.getItem()), "价格: §6" + shop.getPrice() + "§0 E币", "数量: §9" + shop.getItem().getCount() + "§0 个");
                            player.sendTitle(TextFormat.DARK_GREEN + "官方商店设置成功!");
                            break;
                        case 2:
                            int id = ZQUtils.getItemID((String) mainclass.settingHandler.get(player.getName()).get("itemID"));
                            int meta = ZQUtils.getItemMeta((String) mainclass.settingHandler.get(player.getName()).get("itemID"));
                            int count = (int) mainclass.settingHandler.get(player.getName()).get("count");
                            Item shopItem = Item.get(id, meta, count);
                            Inventory inventory = player.getInventory();
                            for (Map.Entry<Integer, Item> entry : inventory.getContents().entrySet()) {
                                Item item = entry.getValue();
                                if (item.equals(shopItem) && item.getCount() >= count) {
                                    inventory.removeItem(shopItem);
                                    mainclass.addPrivateShop((int) mainclass.settingHandler.get(player.getName()).get("id"), block.getLocation(), id + ":" + meta, (int) mainclass.settingHandler.get(player.getName()).get("price"), (String)mainclass.settingHandler.get(player.getName()).get("owner"), count);
                                    PrivateShop shop2 = (PrivateShop) mainclass.getShop(block);
                                    ((BlockEntitySign) block.getLevel().getBlockEntity(block)).setText("§b私人商店", ZQUtils.getItemNameInChinese(shop2.getItem()), "价格: §6" + shop2.getPrice() + "§0 E币 §9" + shop2.getItem().getCount() + "§0 个", "店主: §1" + shop2.getOwner());
                                    player.sendTitle(TextFormat.DARK_GREEN + "私人商店设置成功!");
                                } else {
                                    player.sendTip(TextFormat.RED + "物品数量不足!!!!!!!!!!!!!!!!!!!!!!!!!");
                                }
                            }
                            break;
                        case 3:
                            mainclass.addRecycleShop((int) mainclass.settingHandler.get(player.getName()).get("id"), block.getLocation(), (String) mainclass.settingHandler.get(player.getName()).get("itemID"), (int) mainclass.settingHandler.get(player.getName()).get("price"), (int) mainclass.settingHandler.get(player.getName()).get("count"));
                            RecycleShop shop3 = (RecycleShop) mainclass.getShop(block);
                            ((BlockEntitySign) block.getLevel().getBlockEntity(block)).setText("§7回收站", ZQUtils.getItemNameInChinese(shop3.getItem()), "价格: §6" + shop3.getPrice() + "§0 E币", "数量: §9" + shop3.getItem().getCount() + "§0 个");
                            player.sendTitle(TextFormat.DARK_GREEN + "回收商店设置成功!");
                            break;
                    }
                } else {
                    player.sendTip(TextFormat.RED + "商店已存在!");
                }
            } else {
                player.sendTitle(TextFormat.RED + " 商店设置已取消!");
            }
            mainclass.settingHandler.remove(player.getName());
            event.setCancelled();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onRemovingShop(PlayerInteractEvent event) {
        if(event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_AIR || event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) return;
        Player player = event.getPlayer();
        if (mainclass.removingHandler.containsKey(player.getName()) && mainclass.removingHandler.get(player.getName()) == 1) {
            if (mainclass.getShop(event.getBlock()) != null) {
                Shop shop = mainclass.getShop(event.getBlock());
                if (shop instanceof PublicShop) {
                    if (player.isOp()) {
                        mainclass.removePublicShop(shop.getId(), shop);
                        ((BlockEntitySign) event.getBlock().getLevel().getBlockEntity(event.getBlock())).setText("", "", "", "");
                        event.getPlayer().sendTitle(TextFormat.GREEN + "商店已移除!");
                    } else {
                        event.getPlayer().sendTip(TextFormat.RED + "您没有权限移除一个官方商店!");
                    }
                } else if (shop instanceof PrivateShop) {
                    if (((PrivateShop) shop).getOwner().equals(player.getName())) {
                        mainclass.removePrivateShop(shop.getId(), shop);
                        ((BlockEntitySign) event.getBlock().getLevel().getBlockEntity(event.getBlock())).setText("", "", "", "");
                        player.getInventory().addItem(shop.getItem());
                        event.getPlayer().sendTitle(TextFormat.GREEN + "商店已移除!");
                    } else {
                        event.getPlayer().sendTip(TextFormat.RED + "这个商店属于§c[§f" + ((PrivateShop) shop).getOwner() + "§c]");
                    }
                } else if (shop instanceof RecycleShop) {
                    if (player.isOp()) {
                        mainclass.removeRecycleShop(shop.getId(), shop);
                        ((BlockEntitySign) event.getBlock().getLevel().getBlockEntity(event.getBlock())).setText("", "", "", "");
                        event.getPlayer().sendTitle(TextFormat.GREEN + "商店已移除!");
                    } else {
                        event.getPlayer().sendTip(TextFormat.RED + "您没有权限移除一个回收站!");
                    }
                }
            } else {
                player.sendTip(TextFormat.RED + "您点击的方块不是商店!");
            }
            mainclass.removingHandler.remove(player.getName());
            event.setCancelled();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onBuying(PlayerInteractEvent event) {
        if(event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_AIR || event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) return;
        Player player = event.getPlayer();
        if (mainclass.settingHandler.containsKey(player.getName()) && mainclass.removingHandler.containsKey(player.getName())) {
            event.setCancelled();
            return;
        }
        Block block = event.getBlock();
        if (mainclass.getShop(block) != null ) {
            Shop shop = mainclass.getShop(block);
            if (!mainclass.buyers.containsKey(player)) {
                mainclass.buyers.put(player, shop);
                FormWindowModal window = null;
                if (shop instanceof PublicShop) {
                    window = new FormWindowModal("购买商品", TextFormat.GOLD + "您确定要以 " + shop.getPrice() + " E币的价格购买 " + shop.getItem().getCount() + " 个 " + ZQUtils.getItemNameInChinese(shop.getItem()) + " 吗?", "确定", "取消");
                } else if (shop instanceof PrivateShop) {
                    if (!((PrivateShop) shop).getOwner().equals(player.getName())) {
                        window = new FormWindowModal("购买商品", TextFormat.GOLD + "您确定要以 " + shop.getPrice() + " E币的价格购买 " + shop.getItem().getCount() + " 个 " + ZQUtils.getItemNameInChinese(shop.getItem()) + " 吗?", "确定", "取消");
                    } else {
                        player.sendTip(TextFormat.RED + "不能购买自己的物品!");
                        mainclass.buyers.remove(player);
                        event.setCancelled();
                        return;
                    }
                }else if (shop instanceof RecycleShop) {
                    window = new FormWindowModal("出售商品", TextFormat.GOLD + "您确定要以 " + shop.getPrice() + " E币的价格出售 " + shop.getItem().getCount() + " 个 " + ZQUtils.getItemNameInChinese(shop.getItem()) + " 吗?", "确定", "取消");
                }
                player.showFormWindow(window);
                event.setCancelled();
            } else {
                event.setCancelled();
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onSignChange(SignChangeEvent event) {
        if (mainclass.getShop(event.getBlock()) == null) {
            return;
        }
        if (!event.getPlayer().hasPermission("shop.modify")) {
            event.setCancelled();
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onBlockBreak(BlockBreakEvent event) {
        if (mainclass.getShop(event.getBlock()) == null) {
            return;
        }
        Player player = event.getPlayer();
        Shop shop = mainclass.getShop(event.getBlock());
        if ((shop instanceof PrivateShop && ((PrivateShop) shop).getOwner().equals(player.getName())) || player.hasPermission("shop.destroy")) {
            mainclass.removePrivateShop(shop.getId(), shop);
            player.getInventory().addItem(shop.getItem());
            player.sendTitle(TextFormat.GREEN + "商店已移除!");
        }

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onPlayerQuite(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        mainclass.settingHandler.remove(player.getName());
        mainclass.removingHandler.remove(player.getName());
        mainclass.buyers.remove(player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onPlayerFormResponded(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();
        if (event.getResponse() instanceof FormResponseModal) {
            if (mainclass.buyers.containsKey(player)) {
                Shop shop = mainclass.buyers.get(player);
                if (((FormResponseModal) event.getResponse()).getClickedButtonText().equals("确定")) {
                    if (shop instanceof PublicShop) {
                        if (mainclass.money.getMoney(player, CurrencyType.FIRST) >= shop.getPrice()) {
                            if(player.getInventory().canAddItem(shop.getItem())){
                                mainclass.money.reduceMoney(player, shop.getPrice(), CurrencyType.FIRST);
                                player.getInventory().addItem(shop.getItem());
                                player.sendTip(TextFormat.GREEN + "以 " + shop.getPrice() + " E币的价格购买 " + shop.getItem().getCount() + " 个 " + ZQUtils.getItemNameInChinese(shop.getItem()) + " 成功!");
                            } else {
                                player.sendTip(TextFormat.RED + "背包已满,购买失败!");
                            }
                        } else {
                            player.sendTip(TextFormat.RED + "E币不足,购买失败!");
                        }
                    } else if (shop instanceof PrivateShop) {
                        if (mainclass.money.getMoney(player, CurrencyType.FIRST) >= shop.getPrice()) {
                            if(player.getInventory().canAddItem(shop.getItem())){
                                mainclass.money.reduceMoney(player, shop.getPrice(), CurrencyType.FIRST);
                                player.getInventory().addItem(shop.getItem());
                                mainclass.money.addMoney(((PrivateShop) shop).getOwner(), shop.getPrice(), CurrencyType.FIRST);
                                mainclass.removePrivateShop(shop.getId(), shop);
                                ((BlockEntitySign) shop.getPosition().getLevel().getBlockEntity(shop.getPosition())).setText("§b私人商店", ZQUtils.getItemNameInChinese(shop.getItem()), "§c已售罄！", "店主: §1" + ((PrivateShop) shop).getOwner());
                                player.sendTip(TextFormat.GREEN + "以 " + shop.getPrice() + " E币的价格购买 " + shop.getItem().getCount() + " 个 " + ZQUtils.getItemNameInChinese(shop.getItem()) + " 成功!");
                            } else {
                                player.sendTip(TextFormat.RED + "背包已满,购买失败!");
                            }
                        } else {
                            player.sendTip(TextFormat.RED + "E币不足,购买失败!");
                        }
                    } else if (shop instanceof RecycleShop) {
                        Inventory inventory = player.getInventory();
                        for (Map.Entry<Integer, Item> entry : inventory.getContents().entrySet()) {
                            Item item = entry.getValue();
                            if (item.equals(shop.getItem()) && item.getCount() >= shop.getItem().getCount()) {
                                inventory.removeItem(shop.getItem());
                                mainclass.money.addMoney(player, shop.getPrice(), CurrencyType.FIRST);
                                player.sendTip(TextFormat.GREEN + "以 " + shop.getPrice() + " E币的价格出售 " + shop.getItem().getCount() + " 个 " + ZQUtils.getItemNameInChinese(shop.getItem()) + " 成功!");
                                mainclass.buyers.remove(player);
                                return;
                            } else {
                                player.sendTip(TextFormat.RED + "物品数量不足!");
                            }
                        }
                    }
                }
                mainclass.buyers.remove(player);
            }
        }
    }
}