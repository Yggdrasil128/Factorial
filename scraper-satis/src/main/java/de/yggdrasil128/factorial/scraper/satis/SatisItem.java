package de.yggdrasil128.factorial.scraper.satis;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.math.BigDecimal;
import java.util.Set;

public record SatisItem(@JsonAlias("ClassName") String className,
                        @JsonAlias("mDisplayName") String displayName,
                        @JsonAlias("mDescription") String description,
                        @JsonAlias("mStackSize") StackSize stackSize,
                        @JsonAlias("mEnergyValue") BigDecimal energyValue, // MJ
                        @JsonAlias("mForm") ResourceForm form,
                        @JsonAlias("mFluidColor") StringStruct fluidColor,
                        @JsonAlias("mGasColor") StringStruct gasColor,
                        @JsonAlias("mResourceSinkPoints") int resourceSinkPoints) {
    public static final Set<String> SCRIPT_CLASSES = Set.of("/Script/FactoryGame.FGItemDescriptor",
            "/Script/FactoryGame.FGItemDescriptorNuclearFuel", "/Script/FactoryGame.FGAmmoTypeProjectile",
            "/Script/FactoryGame.FGEquipmentDescriptor", "/Script/FactoryGame.FGAmmoTypeSpreadshot",
            "/Script/FactoryGame.FGAmmoTypeInstantHit", "/Script/FactoryGame.FGItemDescriptorBiomass",
            "/Script/FactoryGame.FGPowerShardDescriptor", "/Script/FactoryGame.FGItemDescriptorPowerBoosterFuel");
}
