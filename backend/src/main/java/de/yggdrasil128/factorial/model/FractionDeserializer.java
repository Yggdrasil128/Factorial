package de.yggdrasil128.factorial.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class FractionDeserializer extends JsonDeserializer<Fraction> {

    @Override
    public Fraction deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.currentTokenId() == JsonTokenId.ID_STRING) {
            return Fraction.of(p.getText());
        }
        return (Fraction) ctxt.handleUnexpectedToken(Fraction.class, p);
    }

}
