package de.randombyte.byteitems.commands

import de.randombyte.byteitems.ByteItems
import de.randombyte.kosp.PlayerExecutedCommand
import de.randombyte.kosp.extensions.green
import de.randombyte.kosp.extensions.toText
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.api.entity.living.player.Player

internal class SaveCommand : PlayerExecutedCommand() {
    override fun executedByPlayer(player: Player, args: CommandContext): CommandResult {
        val id = args.getOne<String>(ByteItems.ITEM_ID_ARG).get()

        val itemStack = player.getItemInHand(HandTypes.MAIN_HAND)
                .orElseThrow { CommandException("Hold the item in your main hand while saving!".toText()) }

        val itemsConfigHolder = ByteItems.INSTANCE.configAccessor.items
        val itemsConfig =  itemsConfigHolder.get()

        if (itemsConfig.items.containsKey(id)) throw CommandException("ID '$id' is already in use!".toText())
        val newItemsConfig = with(itemsConfig) { copy(items = items + (id to itemStack.createSnapshot())) }

        itemsConfigHolder.save(newItemsConfig)
        player.sendMessage("Saved ItemStack '$id'!".green())

        return CommandResult.success()
    }
}