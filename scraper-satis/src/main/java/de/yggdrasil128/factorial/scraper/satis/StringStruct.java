package de.yggdrasil128.factorial.scraper.satis;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = StringStructDeserializer.class)
public record StringStruct(StringStructs.Node root) {
}
