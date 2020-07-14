package cn.yzq25.shop;

import cn.nukkit.level.Position;

public class RecycleShop extends Shop {

    private RecycleShop(int id, Position position, int itemId, int meta, int price, int count) {
        super(id, position, itemId, meta, price, count);
    }

    public RecycleShop(int id, int x, int y, int z, String world, int itemId, int meta, int price, int count) {
        this(id, new Position(x, y, z, ShopMain.getInstance().getServer().getLevelByName(world)), itemId, meta, price, count);
    }
}
