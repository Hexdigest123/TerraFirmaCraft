/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.Helpers;


public record BlastFurnaceRecipe(
    SizedFluidIngredient inputFluid,
    Ingredient catalyst, FluidStack outputFluid
) implements INoopInputRecipe
{
    public static final MapCodec<BlastFurnaceRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        SizedFluidIngredient.FLAT_CODEC.fieldOf("fluid").forGetter(c -> c.inputFluid),
        Ingredient.CODEC.fieldOf("catalyst").forGetter(c -> c.catalyst),
        FluidStack.CODEC.fieldOf("result").forGetter(c -> c.outputFluid)
    ).apply(i, BlastFurnaceRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlastFurnaceRecipe> STREAM_CODEC = StreamCodec.composite(
        SizedFluidIngredient.STREAM_CODEC, c -> c.inputFluid,
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.catalyst,
        FluidStack.STREAM_CODEC, c -> c.outputFluid,
        BlastFurnaceRecipe::new
    );

    /**
     * @return A recipe matching a primary input item stack.
     */
    @Nullable
    public static BlastFurnaceRecipe get(Level level, ItemStack stack)
    {
        final HeatingRecipe heatRecipe = HeatingRecipe.getRecipe(stack);
        if (heatRecipe != null)
        {
            final FluidStack moltenFluid = heatRecipe.assembleFluid(stack);
            for (BlastFurnaceRecipe recipe : Helpers.getRecipes(level, TFCRecipeTypes.BLAST_FURNACE).values())
            {
                if (recipe.inputFluid.ingredient().test(moltenFluid))
                {
                    return recipe;
                }
            }
        }
        return null;
    }

    /**
     * @return A recipe matching just the input fluid, ignoring amounts.
     */
    @Nullable
    public static BlastFurnaceRecipe get(Level level, FluidStack inputFluid)
    {
        for (BlastFurnaceRecipe recipe : Helpers.getRecipes(level, TFCRecipeTypes.BLAST_FURNACE).values())
        {
            if (recipe.inputFluid.ingredient().test(inputFluid))
            {
                return recipe;
            }
        }
        return null;
    }

    public boolean matchesInput(ItemStack stack)
    {
        final HeatingRecipe heat = HeatingRecipe.getRecipe(stack);
        if (heat != null)
        {
            // Ignore count, since the blast furnace will aggregate all inputs
            final FluidStack fluid = heat.assembleFluid(stack);
            return inputFluid.ingredient().test(fluid);
        }
        return false;
    }

    public boolean matchesCatalyst(ItemStack stack)
    {
        return catalyst.test(stack);
    }

    /**
     * Consumes amounts of {@code inputFluid}, and returns the amount of output fluid that was produced.
     *
     * @param inputFluid The input fluid, which will contain the remainder after producing output.
     */
    public FluidStack assembleFluidOutput(FluidStack inputFluid)
    {
        final int maximumRatio = inputFluid.getAmount() / this.inputFluid.amount();
        final FluidStack outputFluid = this.outputFluid.copy();

        inputFluid.shrink(maximumRatio * this.inputFluid.amount());
        outputFluid.setAmount(this.outputFluid.getAmount() * maximumRatio);

        return outputFluid;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.BLAST_FURNACE.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.BLAST_FURNACE.get();
    }
}
