import me.tofaa.itemsystem.Item;
import me.tofaa.itemsystem.ItemProcessor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerHandAnimationEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;

import java.io.File;

public final class Test {

    public static void main(String[] args) {
        var minestom = MinecraftServer.init();

        var fullbright = MinecraftServer.getDimensionTypeRegistry().register(
                NamespaceID.from("tofaa:fullbright"),
                DimensionType.builder().ambientLight(2.0f).build()
        );
        var world = MinecraftServer.getInstanceManager().createInstanceContainer(fullbright);
        world.setGenerator(unit -> {
            unit.modifier().fillHeight(0, 64, Block.GRASS_BLOCK);
        });

        var eh = MinecraftServer.getGlobalEventHandler();
        eh.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(world);
            event.getPlayer().setRespawnPoint(new Pos(0, 65, 0));
        });
        eh.addListener(PlayerHandAnimationEvent.class, event -> {
            Item item = Item.fromItemStack(event.getPlayer().getItemInMainHand());
            CrappyComponent c = item.getComponent(CrappyComponent.class);
            if (c == null) return;
            float durability = c.durability();
            if (durability <= 0) {
                event.getPlayer().sendMessage("This item is broken!");
                return;
            }
            item = item.withComponent(new CrappyComponent(c.displayName(), durability - 1));
            event.getPlayer().setItemInMainHand(item.toItemStack());
        });

        ItemProcessor.init(new File("./items"));
        ItemProcessor.instance().registerHandler(new CrappyComponentHandler());
        ItemProcessor.instance().loadAll();

        MinecraftServer.getCommandManager().register(new ItemCommand());

        minestom.start("localhost", 25565);
    }

}
