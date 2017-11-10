package de.randombyte.byteitems

import org.spongepowered.api.item.inventory.ItemStackSnapshot

interface ByteItemsApi {
    fun getItem(id: String): ItemStackSnapshot?
    fun getItemSafely(id: String, failMessage: String = "The byte-item '$id' could not be found!") =
            getItem(id) ?: throw IllegalArgumentException(failMessage)
}