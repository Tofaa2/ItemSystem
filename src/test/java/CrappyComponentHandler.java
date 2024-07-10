import com.google.gson.JsonElement;
import me.tofaa.itemsystem.ItemComponentHandler;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CrappyComponentHandler implements ItemComponentHandler<CrappyComponent> {
    @Override
    public @NotNull Class<CrappyComponent> componentType() {
        return CrappyComponent.class;
    }

    @Override
    public @NotNull String componentId() {
        return "tofaa:crappy";
    }

    @Override
    public @NotNull CrappyComponent fromJson(String itemId, JsonElement json) {
        return new CrappyComponent(json.getAsJsonObject().get("display").getAsString(), 100.0f);
    }

    @Override
    public @NotNull CrappyComponent fromNbt(String itemId, CompoundBinaryTag nbt) {
        return new CrappyComponent(nbt.getString("display"), nbt.getFloat("durability"));
    }

    @Override
    public CompoundBinaryTag toNbt(String itemId, @NotNull CrappyComponent component) {
        return CompoundBinaryTag.builder()
                .putString("display", component.displayName())
                .putFloat("durability", component.durability()).build();
    }

    @Override
    public void apply(String itemId, @NotNull CrappyComponent component, ItemStack.Builder builder) {
        var name = Component.text(component.displayName())
                        .append(Component.text(" ("))
                        .append(Component.text(component.durability()))
                        .append(Component.text(")"));
        builder.set(ItemComponent.CUSTOM_NAME, name);
    }

    @Override
    public int priority() {
        return 0;
    }
}
