package de.randombyte.byteitems

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.spongepowered.api.item.inventory.ItemStackSnapshot

@ConfigSerializable data class Config(
        @Setting val items: Map<String, ItemStackSnapshot> = emptyMap()
)