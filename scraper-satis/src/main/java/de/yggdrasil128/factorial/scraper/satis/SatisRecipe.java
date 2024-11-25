package de.yggdrasil128.factorial.scraper.satis;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.math.BigDecimal;

public record SatisRecipe(@JsonAlias("ClassName") String className,
                          @JsonAlias("mDisplayName") String displayName,
                          @JsonAlias("mIngredients") StringStruct ingredients,
                          @JsonAlias("mProduct") StringStruct products,
                          @JsonAlias("mManufactoringDuration") BigDecimal duration,
                          @JsonAlias("mProducedIn") StringStruct producedIn) {
    public static final String SCRIPT_CLASS = "/Script/FactoryGame.FGRecipe";
}
