package de.randombyte.byteitems.commands

import de.randombyte.kosp.PlayerExecutedCommand
import de.randombyte.kosp.extensions.*
import de.randombyte.kosp.getServiceOrFail
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.service.pagination.PaginationService
import org.spongepowered.api.text.action.TextActions

internal class ListCommand(val items: Map<String, ItemStackSnapshot>) : PlayerExecutedCommand() {
    override fun executedByPlayer(player: Player, args: CommandContext): CommandResult {
        val texts = items.toSortedMap().map { (id, itemStackSnapshot) ->
            "- '$id': ${itemStackSnapshot.type.id} ".toText() +
                    "[Give] ".yellow().action(TextActions.suggestCommand("/byteItems give ${player.name} \"$id\"")) +
                    "[Delete]".red().action(TextActions.suggestCommand("/byteItems delete \"$id\""))
        }

        getServiceOrFail(PaginationService::class).builder()
                .title("ByteItems".aqua())
                .contents(texts)
                .sendTo(player)

        return CommandResult.queryResult(texts.size) // maybe someone wants to use it
    }
}