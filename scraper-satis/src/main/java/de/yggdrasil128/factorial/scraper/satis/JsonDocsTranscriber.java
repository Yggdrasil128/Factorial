package de.yggdrasil128.factorial.scraper.satis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.yggdrasil128.factorial.model.game.GameSummary;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonDocsTranscriber {

    public static void main(String[] args) {
        Path dir = Paths.get(args[0]);
        try {
            transcribe(dir.resolve("origin.json"), "Satisfactory 1.0", dir.resolve("Satisfactory1.0.json"));
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public static void transcribe(Path source, String gameName, Path target) throws IOException {
        SatisGame parsed = SatisGame.from(source, gameName);
        FactorialGame interpreted = FactorialGame.from(parsed);
        GameSummary summary = interpreted.toSummary();
        try (Writer out = Files.newBufferedWriter(target)) {
            new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true).writeValue(out, summary);
        }
    }

}
