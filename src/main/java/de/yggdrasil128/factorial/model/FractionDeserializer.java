package de.yggdrasil128.factorial.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class FractionDeserializer extends JsonDeserializer<Fraction> {

    @Override
    public Fraction deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        switch (p.currentTokenId()) {
        case JsonTokenId.ID_STRING:
            return Fraction.of(p.getText());
        default:
            return (Fraction) ctxt.handleUnexpectedToken(Fraction.class, p);
        }
    }

}
