package me.tofaa.itemsystem;

import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

record ItemImpl(
        NamespaceID id,
        Material material,
        int amount,
        ItemComponent[] compArray
) implements Item {
    @Override
    public @NotNull Item withAmount(int amount) {
        return new ItemImpl(id, material, amount, compArray);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ItemImpl item = (ItemImpl) obj;
        return id.equals(item.id) && material == item.material && amount == item.amount;
    }

    @Override
    public @NotNull Item withComponent(@NotNull ItemComponent component) {
        if (compArray.length == 0) {
            return new ItemImpl(id, material, amount, new ItemComponent[]{component});
        }
        int index = -1;
        for (int i = 0; i < compArray.length; i++) {
            if (compArray[i].getClass().equals(component.getClass())) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            ItemComponent[] newComponents = new ItemComponent[compArray.length + 1];
            System.arraycopy(compArray, 0, newComponents, 0, compArray.length);
            newComponents[compArray.length] = component;
            return new ItemImpl(id, material, amount, newComponents);
        }
        compArray[index] = component;
        return new ItemImpl(id, material, amount, compArray);
    }

    @Override
    public <C extends ItemComponent> @Nullable C getComponent(@NotNull Class<C> componentType) {
        for (ItemComponent component : compArray) {
            if (component.getClass().equals(componentType)) {
                return (C) component;
            }
        }
        return null;
    }

    @Override
    public @NotNull Item withoutComponent(@NotNull Class<? extends ItemComponent> componentType) {
        int index = -1;
        for (int i = 0; i < compArray.length; i++) {
            if (compArray[i].getClass().equals(componentType)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return this;
        }
        ItemComponent[] newComponents = new ItemComponent[compArray.length - 1];
        System.arraycopy(compArray, 0, newComponents, 0, index);
        System.arraycopy(compArray, index + 1, newComponents, index, compArray.length - index - 1);
        return new ItemImpl(id, material, amount, newComponents);
    }

    @Override
    public @NotNull Stream<ItemComponent> components() {
        return Stream.of(compArray);
    }

}
