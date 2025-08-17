package de.yggdrasil128.factorial.scraper.satis;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.math.BigDecimal;

public record SatisResource(@JsonAlias("ClassName") String className,
                            @JsonAlias("mDisplayName") String displayName,
                            @JsonAlias("mDescription") String description,
                            @JsonAlias("mStackSize") StackSize stackSize,
                            @JsonAlias("mEnergyValue") BigDecimal energyValue, // MJ
                            @JsonAlias("mForm") ResourceForm form,
                            @JsonAlias("mFluidColor") StringStruct fluidColor,
                            @JsonAlias("mGasColor") StringStruct gasColor,
                            @JsonAlias("mResourceSinkPoints") int resourceSinkPoints) {
    public static final String SCRIPT_CLASS = "/Script/FactoryGame.FGResourceDescriptor";
}
