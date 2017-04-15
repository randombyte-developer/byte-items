package de.randombyte.byteitems.commands

import de.randombyte.byteitems.ByteItems
import de.randombyte.kosp.extensions.green
import de.randombyte.kosp.extensions.toText
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.api.entity.EntityTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.NamedCause
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult.Type.SUCCESS

class GiveCommand(
        val plugin: ByteItems,
        val getItemStack: (id: String) -> ItemStackSnapshot?
) : CommandExecutor {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val targetPlayer = args.getOne<Player>(ByteItems.PLAYER_ARG).get()
        val id = args.getOne<String>(ByteItems.ID_ARG).get()

        val itemStackSnapshot = getItemStack(id) ?:
                throw CommandException("Item '$id' is not available!".toText())

        val isPlayerHoldingSomething = targetPlayer.getItemInHand(HandTypes.MAIN_HAND).isPresent
        if (!isPlayerHoldingSomething) {
            // nothing in hand -> put item in hand
            targetPlayer.setItemInHand(HandTypes.MAIN_HAND, itemStackSnapshot.createStack())
        } else {
            // something in hand -> place item somewhere in inventory
            val transactionResult = targetPlayer.inventory.offer(itemStackSnapshot.createStack())
            if (transactionResult.type != SUCCESS) {
                // inventory full -> spawn as item
                val entity = targetPlayer.location.extent.createEntity(EntityTypes.ITEM, targetPlayer.location.position)
                entity.offer(Keys.REPRESENTED_ITEM, itemStackSnapshot)
                if (!targetPlayer.location.extent.spawnEntity(entity, Cause.of(NamedCause.source(plugin)))) {
                    throw CommandException("Couldn't spawn Item!".toText())
                }
            }
        }

        targetPlayer.sendMessage("Given '$id' to ${targetPlayer.name}!".green())

        return CommandResult.success()
    }
}