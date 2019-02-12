package de.randombyte.byteitems.config

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.spongepowered.api.item.inventory.ItemStackSnapshot

@ConfigSerializable data class ItemsConfig(
        @Setting val items: Map<String, ItemStackSnapshot> = emptyMap()
)