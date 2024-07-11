/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.entities.Fauna;
import net.dries007.tfc.util.climate.ClimateRange;

public final class DataManagers
{
    public static final ResourceKey<Registry<DataManager<?>>> KEY = ResourceKey.createRegistryKey(Helpers.identifier("data_manager"));
    public static final Registry<DataManager<?>> REGISTRY = new RegistryBuilder<>(KEY).sync(true).create();

    public static final DeferredRegister<DataManager<?>> MANAGERS = DeferredRegister.create(KEY, TerraFirmaCraft.MOD_ID);

    static
    {
        register(EntityDamageResistance.MANAGER);
        register(ItemDamageResistance.MANAGER);
        register(Drinkable.MANAGER);
        register(Fertilizer.MANAGER);
        register(Fuel.MANAGER);
        register(Metal.MANAGER);
        register(KnappingType.MANAGER);
        register(Support.MANAGER);

        register(Pannable.MANAGER);
        register(Sluiceable.MANAGER);
        register(LampFuel.MANAGER);
        register(ItemSizeManager.MANAGER);
        register(ClimateRange.MANAGER);
        register(Fauna.MANAGER);
        register(HeatCapability.MANAGER);
        register(FoodCapability.MANAGER);
    }

    private static void register(DataManager<?> manager)
    {
        MANAGERS.register(manager.typeName, () -> manager);
    }
}
