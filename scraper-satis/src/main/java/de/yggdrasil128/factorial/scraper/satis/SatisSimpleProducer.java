package de.yggdrasil128.factorial.scraper.satis;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.math.BigDecimal;

public record SatisSimpleProducer(@JsonAlias("ClassName") String className,
                                  @JsonAlias("mTimeToProduceItem") BigDecimal timeToProduceItem,
                                  @JsonAlias("mDisplayName") String displayName,
                                  @JsonAlias("mDescription") String description) {
    public static final String SCRIPT_CLASS = "/Script/FactoryGame.FGBuildableFactorySimpleProducer";
    // this relation is not described in the community JSON, we have to hardcode it
    public static final String PRODUCED_ITEM = "Desc_Gift_C";
}
