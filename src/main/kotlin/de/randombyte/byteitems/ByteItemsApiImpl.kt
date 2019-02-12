package de.randombyte.byteitems

import de.randombyte.byteitems.config.ItemsConfig

class ByteItemsApiImpl(val getConfig: () -> ItemsConfig) : ByteItemsApi {
    override fun getItem(id: String) = getConfig().items[id.removePrefix(getByteItemsPrefix() + ":")]
}