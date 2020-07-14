package cn.yzq25.shop;

import cn.nukkit.level.Position;

public class PrivateShop extends Shop {
    private String owner;

    public PrivateShop(int id, Position position, int itemId, int meta, int price, String owner, int count) {
        super(id, position, itemId, meta, price, count);
        this.owner = owner;
    }

    public PrivateShop(int id, int x, int y, int z, String world, int itemId, int meta, int price, String owner,int count) {
        this(id, new Position(x, y, z, ShopMain.getInstance().getServer().getLevelByName(world)), itemId, meta, price, owner, count);
    }

    public String getOwner() {
        return this.owner;
    }
}
