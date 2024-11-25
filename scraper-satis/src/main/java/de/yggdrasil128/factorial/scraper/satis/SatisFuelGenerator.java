package de.yggdrasil128.factorial.scraper.satis;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.math.BigDecimal;
import java.util.Set;

public record SatisFuelGenerator(@JsonAlias("ClassName") String className,
                                 @JsonAlias("mFuel") Fuel[] fuels,
                                 @JsonAlias("mFuelLoadAmount") int fuelLoadAmount,
                                 @JsonAlias("mRequiresSupplementalResource") boolean requiresSupplementalResource,
                                 @JsonAlias("mSupplementalLoadAmount") int supplementalLoadAmount,
                                 @JsonAlias("mPowerProduction") BigDecimal powerProduction,
                                 @JsonAlias("mDisplayName") String displayName,
                                 @JsonAlias("mDescription") String description) {

    public static final Set<String> SCRIPT_CLASSES = Set.of("/Script/FactoryGame.FGBuildableGeneratorFuel",
            "/Script/FactoryGame.FGBuildableGeneratorNuclear");

    public record Fuel(@JsonAlias("mFuelClass") String fuelClass,
                       @JsonAlias("mSupplementalResourceClass") String supplementalResourceClass,
                       @JsonAlias("mByproduct") String byproduct,
                       @JsonAlias("mByproductAmount") int byproductAmount) {
    }

}
