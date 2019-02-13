package de.randombyte.byteitems

import de.randombyte.byteitems.api.ByteItemsService
import de.randombyte.byteitems.config.ItemsConfig
import de.randombyte.kosp.extensions.toOptional

class ByteItemsServiceImpl(val getConfig: () -> ItemsConfig) : ByteItemsService {
    override fun get(id: String) = getConfig().items[id.removePrefix("$prefix:")].toOptional()
    override fun getPrefix() = ByteItems.ID
}