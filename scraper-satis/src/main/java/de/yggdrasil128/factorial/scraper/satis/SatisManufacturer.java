package de.yggdrasil128.factorial.scraper.satis;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.Set;

public record SatisManufacturer(@JsonAlias("ClassName") String className,
                                @JsonAlias("mDisplayName") String displayName,
                                @JsonAlias("mDescription") String description) {
    public static final Set<String> SCRIPT_CLASSES = Set.of("/Script/FactoryGame.FGBuildableManufacturerVariablePower",
            "/Script/FactoryGame.FGBuildableManufacturer");
}
