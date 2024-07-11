/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component;

import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

/**
 * Provides a mutable view of a specific component attached to an item stack. This <strong>assumes ownership</strong> over any
 * changes to the component, and you should never create multiple separate views for the same stack at a time.
 */
public abstract class ComponentView<T>
{
    private final Supplier<DataComponentType<T>> type;
    private final ItemStack stack;
    protected T component;

    protected ComponentView(ItemStack stack, Supplier<DataComponentType<T>> type, T defaultValue)
    {
        this.type = type;
        this.stack = stack;
        this.component = stack.getOrDefault(type, defaultValue);
    }

    public T view()
    {
        return component;
    }

    protected void apply(T newValue)
    {
        if (!stack.isEmpty()) stack.set(type, component = newValue);
    }
}
