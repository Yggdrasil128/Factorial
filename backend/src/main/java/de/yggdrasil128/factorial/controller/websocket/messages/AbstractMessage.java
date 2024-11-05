package de.yggdrasil128.factorial.controller.websocket.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = InitialMessage.class, name = "initial"),

        @JsonSubTypes.Type(value = GamesReorderedMessage.class, name = "gamesReordered"),
        @JsonSubTypes.Type(value = SavesReorderedMessage.class, name = "savesReordered"),
        @JsonSubTypes.Type(value = SaveUpdatedMessage.class, name = "saveUpdated"),
        @JsonSubTypes.Type(value = SaveRemovedMessage.class, name = "saveRemoved"),
        @JsonSubTypes.Type(value = FactoriesReorderedMessage.class, name = "factoriesReordered"),
        @JsonSubTypes.Type(value = FactoryUpdatedMessage.class, name = "factoryUpdated"),
        @JsonSubTypes.Type(value = FactoryRemovedMessage.class, name = "factoryRemoved"),
        @JsonSubTypes.Type(value = ChangelistsReorderedMessage.class, name = "changelistsReordered"),
        @JsonSubTypes.Type(value = ChangelistUpdatedMessage.class, name = "changelistUpdated"),
        @JsonSubTypes.Type(value = ChangelistRemovedMessage.class, name = "changelistRemoved"),
        @JsonSubTypes.Type(value = ProductionStepUpdatedMessage.class, name = "productionStepUpdated"),
        @JsonSubTypes.Type(value = ProductionStepRemovedMessage.class, name = "productionStepRemoved"),
        @JsonSubTypes.Type(value = ResourcesReorderedMessage.class, name = "resourcesReordered"),
        @JsonSubTypes.Type(value = ResourceUpdatedMessage.class, name = "resourceUpdated"),
        @JsonSubTypes.Type(value = ResourceRemovedMessage.class, name = "resourceRemoved"),
})
public abstract class AbstractMessage {
    private final String runtimeId;
    private final int messageId;

    public AbstractMessage(String runtimeId, int messageId) {
        this.runtimeId = runtimeId;
        this.messageId = messageId;
    }

    public String getRuntimeId() {
        return runtimeId;
    }

    public int getMessageId() {
        return messageId;
    }
}
