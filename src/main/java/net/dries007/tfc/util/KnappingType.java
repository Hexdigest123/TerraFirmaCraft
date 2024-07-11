/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Optional;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Function7;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
import net.dries007.tfc.network.DataManagerSyncPacket;
import net.dries007.tfc.network.PacketCodecs;

public record KnappingType(
    SizedIngredient inputItem,
    int amountToConsume,
    Holder<SoundEvent> clickSound,
    boolean consumeAfterComplete,
    boolean useDisabledTexture,
    boolean spawnsParticles,
    ItemStack jeiIconItem
)
{
    public static final Codec<KnappingType> CODEC = RecordCodecBuilder.create(i -> i.group(
        SizedIngredient.FLAT_CODEC.fieldOf("input").forGetter(c -> c.inputItem),
        Codec.INT.optionalFieldOf("amount_to_consume").forGetter(c -> c.amountToConsume == c.inputItem.count() ? Optional.empty() : Optional.of(c.amountToConsume)),
        SoundEvent.CODEC.fieldOf("click_sound").forGetter(c -> c.clickSound),
        Codec.BOOL.fieldOf("consume_after_complete").forGetter(c -> c.consumeAfterComplete),
        Codec.BOOL.fieldOf("use_disabled_texture").forGetter(c -> c.useDisabledTexture),
        Codec.BOOL.fieldOf("spawns_particles").forGetter(c -> c.spawnsParticles),
        ItemStack.CODEC.fieldOf("jei_icon_item").forGetter(c -> c.jeiIconItem)
    ).apply(i, KnappingType::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, KnappingType> STREAM_CODEC = PacketCodecs.composite(
        SizedIngredient.STREAM_CODEC, c -> c.inputItem,
        ByteBufCodecs.VAR_INT, c -> c.amountToConsume,
        ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT), c -> c.clickSound,
        ByteBufCodecs.BOOL, c -> c.consumeAfterComplete,
        ByteBufCodecs.BOOL, c -> c.useDisabledTexture,
        ByteBufCodecs.BOOL, c -> c.spawnsParticles,
        ItemStack.STREAM_CODEC, c -> c.jeiIconItem,
        KnappingType::new
    );

    public static final DataManager<KnappingType> MANAGER = new DataManager<>(Helpers.identifier("knapping_types"), "knapping_types", CODEC, STREAM_CODEC);

    @Nullable
    public static KnappingType get(Player player)
    {
        final ItemStack stack = player.getMainHandItem();
        for (KnappingType type : MANAGER.getValues())
        {
            if (type.inputItem.test(stack))
            {
                return type;
            }
        }
        return null;
    }

    public KnappingType(SizedIngredient inputItem, Optional<Integer> amountToConsume, Holder<SoundEvent> clickSound, boolean consumeAfterComplete, boolean useDisabledTexture, boolean spawnsParticles, ItemStack jeiIconItem)
    {
        this(inputItem, amountToConsume.orElse(inputItem.count()), clickSound, consumeAfterComplete, useDisabledTexture, spawnsParticles, jeiIconItem);
    }
}
