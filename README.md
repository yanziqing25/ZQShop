# ZQShop
### 介绍
本插件为一款集合了官方商店、回收商店、私有插件、UI界面的Nukkit插件!  
注意：此插件需要前置插件—`ZQExtension.jar`和`money.jar`
### 指令
- `/addpublicshop <int: item_id:[item_meta]> <int: price> <int: count>` 添加一个官方商店(简写:`/apbs`)
- `/addrecycleshop <int: item_id:[item_meta]> <int: price> <int: count>` 添加一个回收商店(简写:`/ars`)
- `/addprivateshop <int: item_id:[item_meta]> <int: price> <int: count>` 添加一个私人商店(简写:`/apvs`)
- `/cancelshop` 取消设置商店模式(简写:`/cs`)
- `/removeshop` 移除一个商店(简写:`/rs`)
### 权限
		shop.command.addpublicshop:
			default: op
		shop.command.addprivateshop:
			default: true
		shop.command.addrecycleshop:
			default: op
		shop.command.removeshop:
			default: true
		shop.command.cancelshop:
			default: true

		shop.destroy:
			default: op
		shop.modify:
			default: op