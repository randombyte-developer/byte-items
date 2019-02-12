package de.randombyte.byteitems

import org.spongepowered.api.Sponge
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot

@Deprecated("Use the new ByteItemsService instead.")
interface ByteItemsApi {
    /**
     * Returns the ItemStackSnapshot saved under the given [id].
     * @id "byte-items:testItem1" or "itemToTest4", so without or with [prefix](getByteItemsPrefix)
     */
    fun getItem(id: String): ItemStackSnapshot?

    // I would rather use @JvmOverloads but that is not supported for abstract functions

    fun getItemSafely(id: String) = getItemSafely(id, failMessage = "Couldn't resolve ID '$id'!")

    /**
     * Returns the saved ItemStackSnapshot with the given [id], or fall back to vanilla/modded items
     * identified with '<modId>:<itemId>' with a quantity of 1.
     *
     * @see getItem
     */
    fun getItemSafely(id: String, failMessage: String): ItemStackSnapshot = if (id.startsWith(getByteItemsPrefix())) {
        getItem(id) ?: throw IllegalArgumentException(failMessage)
    } else {
        // fall back to normal minecraft item types
        val itemType = Sponge.getRegistry().getType(ItemType::class.java, id)
                .orElseThrow { IllegalArgumentException(failMessage) }
        ItemStack.of(itemType, 1).createSnapshot()
    }

    fun getByteItemsPrefix() = ByteItems.ID
}