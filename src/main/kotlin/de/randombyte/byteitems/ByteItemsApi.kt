package de.randombyte.byteitems

import org.spongepowered.api.item.inventory.ItemStackSnapshot

interface ByteItemsApi {
    fun getItem(id: String): ItemStackSnapshot?
}