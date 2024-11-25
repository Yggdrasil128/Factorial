package de.yggdrasil128.factorial.scraper.satis;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class StringStructDeserializer extends JsonDeserializer<StringStruct> {

    @Override
    public StringStruct deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        return JsonTokenId.ID_STRING != p.currentTokenId()
                ? (StringStruct) ctxt.handleUnexpectedToken(StringStruct.class, p)
                : new StringStruct(StringStructs.parseStringStruct(p.getText()));
    }

}
