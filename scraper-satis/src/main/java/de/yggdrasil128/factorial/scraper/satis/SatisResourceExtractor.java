package de.yggdrasil128.factorial.scraper.satis;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.math.BigDecimal;
import java.util.Set;

public record SatisResourceExtractor(@JsonAlias("ClassName") String className,
                                     @JsonAlias("mExtractCycleTime") BigDecimal extractCycleTime,
                                     @JsonAlias("mItemsPerCycle") int itemsPerCycle,
                                     @JsonAlias("mAllowedResourceForms") StringStruct allowedResourceForms,
                                     @JsonAlias("mOnlyAllowCertainResources") boolean onlyAllowCertainResources,
                                     @JsonAlias("mAllowedResources") StringStruct allowedResources,
                                     @JsonAlias("mDisplayName") String displayName,
                                     @JsonAlias("mDescription") String description) {
    public static final Set<String> SCRIPT_CLASSES = Set.of("/Script/FactoryGame.FGBuildableWaterPump",
            "/Script/FactoryGame.FGBuildableResourceExtractor", "/Script/FactoryGame.FGBuildableFrackingExtractor");
}
