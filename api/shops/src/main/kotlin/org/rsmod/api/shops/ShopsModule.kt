package org.rsmod.api.shops

import org.rsmod.api.shops.operation.ShopOperationMap
import org.rsmod.api.shops.operation.StandardGpShopOperations
import org.rsmod.api.shops.restock.ShopRestockProcessor
import org.rsmod.plugin.module.PluginModule

public class ShopsModule : PluginModule() {
    override fun bind() {
        bindInstance<ShopOperationMap>()
        bindInstance<ShopRestockProcessor>()
        bindInstance<Shops>()

        bindInstance<StandardGpShopOperations>()
    }
}
