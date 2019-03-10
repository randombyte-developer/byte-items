package de.randombyte.byteitems.commands

import de.randombyte.byteitems.ByteItems
import de.randombyte.byteitems.Messages
import de.randombyte.byteitems.api.ByteItemsService
import de.randombyte.kosp.extensions.*
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.entity.living.player.Player

class GiveCommand : CommandExecutor {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val targetPlayer = args.getOne<Player>(ByteItems.PLAYER_ARG).get()
        val itemId = args.getOne<String>(ByteItems.ITEM_ID_ARG).get()
        val amount = args.getOne<Int>(ByteItems.AMOUNT_ARG).orNull()
        val silentFlag = args.hasAny(ByteItems.SILENT_FLAG)

        val itemStack = ByteItemsService::class.getServiceOrFail().get(itemId).orElseThrow {
            CommandException(Messages.itemNotAvailable(itemId).toText())
        }.createStack()

        if (amount != null) {
            itemStack.quantity = amount.coerceAtLeast(1)
        }

        targetPlayer.give(itemStack)
        if (!silentFlag) targetPlayer.sendMessage("Given '$itemId' to ${targetPlayer.name}!".green())

        return CommandResult.success()
    }
}