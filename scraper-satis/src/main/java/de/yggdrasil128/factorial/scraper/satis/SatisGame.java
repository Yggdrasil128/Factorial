package de.yggdrasil128.factorial.scraper.satis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.UTF_16;

public class SatisGame {

    public static SatisGame from(Path source, String name) throws IOException {
        SatisGame game = new SatisGame(name);
        try (Reader in = Files.newBufferedReader(source, UTF_16)) {
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    false);
            for (JsonNode rootEntry : mapper.readTree(in)) {
                QualifiedClass nativeClass = QualifiedClass.of(rootEntry.get("NativeClass").asText());
                if (SatisItem.SCRIPT_CLASSES.contains(nativeClass.name().qualified())) {
                    handleClasses(mapper, rootEntry, SatisItem.class,
                            item -> game.getItems().put(item.className(), item));
                } else if (SatisResource.SCRIPT_CLASS.equals(nativeClass.name().qualified())) {
                    handleClasses(mapper, rootEntry, SatisResource.class,
                            resource -> game.getResources().put(resource.className(), resource));
                } else if (SatisResourceExtractor.SCRIPT_CLASSES.contains(nativeClass.name().qualified())) {
                    handleClasses(mapper, rootEntry, SatisResourceExtractor.class, resourceExtractor -> game
                            .getResourceExtractors().put(resourceExtractor.className(), resourceExtractor));
                } else if (SatisManufacturer.SCRIPT_CLASSES.contains(nativeClass.name().qualified())) {
                    handleClasses(mapper, rootEntry, SatisManufacturer.class,
                            manufacturer -> game.getManufacturers().put(manufacturer.className(), manufacturer));
                } else if (SatisFuelGenerator.SCRIPT_CLASSES.contains(nativeClass.name().qualified())) {
                    handleClasses(mapper, rootEntry, SatisFuelGenerator.class,
                            fuelGenerator -> game.getFuelGenerators().put(fuelGenerator.className(), fuelGenerator));
                } else if (SatisRecipe.SCRIPT_CLASS.equals(nativeClass.name().qualified())) {
                    handleClasses(mapper, rootEntry, SatisRecipe.class,
                            recipe -> game.getRecipes().put(recipe.className(), recipe));
                }
            }
        }
        return game;
    }

    private static <T> void handleClasses(ObjectMapper mapper, JsonNode rootEntry, Class<T> type,
                                          Consumer<? super T> sink)
            throws JsonProcessingException {
        for (JsonNode clasz : rootEntry.get("Classes")) {
            sink.accept(mapper.treeToValue(clasz, type));
        }
    }

    private final String name;
    private final Map<String, SatisItem> items = new HashMap<>();
    private final Map<String, SatisResource> resources = new HashMap<>();
    private final Map<String, SatisResourceExtractor> resourceExtractors = new HashMap<>();
    private final Map<String, SatisManufacturer> manufacturers = new HashMap<>();
    private final Map<String, SatisFuelGenerator> fuelGenerators = new HashMap<>();
    private final Map<String, SatisRecipe> recipes = new HashMap<>();

    public SatisGame(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map<String, SatisItem> getItems() {
        return items;
    }

    public Map<String, SatisResource> getResources() {
        return resources;
    }

    public Map<String, SatisResourceExtractor> getResourceExtractors() {
        return resourceExtractors;
    }

    public Map<String, SatisManufacturer> getManufacturers() {
        return manufacturers;
    }

    public Map<String, SatisFuelGenerator> getFuelGenerators() {
        return fuelGenerators;
    }

    public Map<String, SatisRecipe> getRecipes() {
        return recipes;
    }
}
