package de.randombyte.byteitems

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import org.spongepowered.api.item.inventory.ItemStackSnapshot

internal class ItemManager(val configurationLoader: ConfigurationLoader<CommentedConfigurationNode>) {

    private companion object {
        val TYPE_TOKEN = object : TypeToken<Map<String, ItemStackSnapshot>>(){}
    }

    internal fun get(id: String) = getAll()[id]

    internal fun put(id: String, itemStackSnapshot: ItemStackSnapshot) {
        save(getAll() + (id to itemStackSnapshot))
    }

    internal fun remove(id: String) {
        save(getAll() - id)
    }

    internal fun has(id: String) = getAll().containsKey(id)

    internal fun getAll(): Map<String, ItemStackSnapshot> = configurationLoader.load().getValue(TYPE_TOKEN, emptyMap())

    private fun save(itemStackSnapshots: Map<String, ItemStackSnapshot>) {
        configurationLoader.run { save(load().setValue(TYPE_TOKEN, itemStackSnapshots)) }
    }
}