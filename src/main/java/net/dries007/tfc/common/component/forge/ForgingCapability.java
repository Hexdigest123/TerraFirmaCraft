/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.forge;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class ForgingCapability
{
    public static Forging get(ItemStack stack)
    {
        return new Forging(stack);
    }

    public static void addTooltipInfo(ItemStack stack, List<Component> tooltips)
    {
        final Forging forging = get(stack);
        if (forging.view().steps().any())
        {
            tooltips.add(Component.translatable("tfc.tooltip.anvil_has_been_worked"));
        }
    }

    public static void clearRecipeIfNotWorked(ItemStack stack)
    {
        get(stack).clearRecipeIfNotWorked();
    }
}