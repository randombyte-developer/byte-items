package de.randombyte.byteitems.commands

import de.randombyte.byteitems.ByteItems
import de.randombyte.byteitems.Messages
import de.randombyte.kosp.PlayerExecutedCommand
import de.randombyte.kosp.extensions.executeCommand
import de.randombyte.kosp.extensions.green
import de.randombyte.kosp.extensions.toText
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player

class DeleteCommand : PlayerExecutedCommand() {
    override fun executedByPlayer(player: Player, args: CommandContext): CommandResult {
        val id = args.getOne<String>(ByteItems.ITEM_ID_ARG).get()

        val itemsConfigHolder = ByteItems.INSTANCE.configAccessor.items
        val itemsConfig = itemsConfigHolder.get()

        if (!itemsConfig.items.containsKey(id)) throw CommandException(Messages.itemNotAvailable(id).toText())
        val newItemsConfig = with(itemsConfig) { copy(items = items - id) }

        itemsConfigHolder.save(newItemsConfig)
        player.sendMessage("Deleted ByteItem '$id'!".green())
        player.executeCommand("byteItems list")

        return CommandResult.success()
    }
}