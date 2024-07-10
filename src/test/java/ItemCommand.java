import me.tofaa.itemsystem.ItemProcessor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;

public class ItemCommand extends Command {

    ItemCommand() {
        super("item");
        Argument<String> idArg = ArgumentType.String("item_id").setSuggestionCallback((sender, context, suggestion) -> {
           var processor = ItemProcessor.instance();
           processor.forEach(item -> {
               suggestion.addEntry(new SuggestionEntry(item.id().asString()));
           });
        });
        addSyntax((sender, context) -> {
            var itemId = context.get(idArg);
            var processor = ItemProcessor.instance();
            var item = processor.getItem(itemId);
            if (item.isAir()) {
                sender.sendMessage("Item not found");
                return;
            }
            sender.asPlayer().getInventory().addItemStack(item.toItemStack());
        }, idArg);
    }

}
