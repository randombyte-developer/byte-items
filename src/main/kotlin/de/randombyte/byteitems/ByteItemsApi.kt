package de.randombyte.byteitems

import org.spongepowered.api.item.inventory.ItemStackSnapshot

interface ByteItemsApi {
    fun getItem(id: String): ItemStackSnapshot?
    // I would rather use @JvmOverloads but that is not supported for abstract functions
    fun getItemSafely(id: String) = getItemSafely(id, failMessage = "The byte-item '$id' could not be found!")
    fun getItemSafely(id: String, failMessage: String) = getItem(id) ?: throw IllegalArgumentException(failMessage)
}