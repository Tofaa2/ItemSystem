package me.tofaa.itemsystem;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public interface Item {

    static @NotNull Item fromItemStack(@NotNull ItemStack stack) {
        return ItemProcessor.instance().fromItemStack(stack);
    }

    Item AIR = new ItemImpl(NamespaceID.from("minecraft:air"), Material.AIR, 0, null);

    @NotNull NamespaceID id();

    @NotNull Material material();

    int amount();

    @NotNull Item withAmount(int amount);

    @NotNull Item withComponent(@NotNull ItemComponent component);

    @NotNull Item withoutComponent(@NotNull Class<? extends ItemComponent> componentType);

    @Nullable <C extends ItemComponent> C getComponent(@NotNull Class<C> componentType);

    @NotNull
    Stream<ItemComponent> components();

    default boolean isAir() {
        return this == AIR;
    }

    default boolean hasComponent(@NotNull Class<? extends ItemComponent> componentType) {
        return getComponent(componentType) != null;
    }

    @NotNull
    default ItemStack toItemStack() {
        return ItemProcessor.instance().toItemStack(this);
    }

}
