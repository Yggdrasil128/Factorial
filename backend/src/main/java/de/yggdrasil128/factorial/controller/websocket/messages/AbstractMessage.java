package de.yggdrasil128.factorial.controller.websocket.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = InitialMessage.class, name = "initial"),
        @JsonSubTypes.Type(value = GamesReorderedMessage.class, name = "gamesReordered"),
        @JsonSubTypes.Type(value = GameUpdatedMessage.class, name = "gameUpdated"),
        @JsonSubTypes.Type(value = GameRemovedMessage.class, name = "gameRemoved"),
        @JsonSubTypes.Type(value = IconUpdatedMessage.class, name = "iconUpdated"),
        @JsonSubTypes.Type(value = IconRemovedMessage.class, name = "iconRemoved"),
        @JsonSubTypes.Type(value = ItemUpdatedMessage.class, name = "itemUpdated"),
        @JsonSubTypes.Type(value = ItemRemovedMessage.class, name = "itemRemoved"),
        @JsonSubTypes.Type(value = RecipeUpdatedMessage.class, name = "recipeUpdated"),
        @JsonSubTypes.Type(value = RecipeRemovedMessage.class, name = "recipeRemoved"),
        @JsonSubTypes.Type(value = RecipeModifierUpdatedMessage.class, name = "recipeModifierUpdated"),
        @JsonSubTypes.Type(value = RecipeModifierRemovedMessage.class, name = "recipeModifierRemoved"),
        @JsonSubTypes.Type(value = MachineUpdatedMessage.class, name = "machineUpdated"),
        @JsonSubTypes.Type(value = MachineRemovedMessage.class, name = "machineRemoved"),
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
        @JsonSubTypes.Type(value = LocalResourcesReorderedMessage.class, name = "localResourcesReordered"),
        @JsonSubTypes.Type(value = LocalResourceUpdatedMessage.class, name = "localResourceUpdated"),
        @JsonSubTypes.Type(value = LocalResourceRemovedMessage.class, name = "localResourceRemoved"),
        @JsonSubTypes.Type(value = GlobalResourcesReorderedMessage.class, name = "globalResourcesReordered"),
        @JsonSubTypes.Type(value = GlobalResourceUpdatedMessage.class, name = "globalResourceUpdated"),
        @JsonSubTypes.Type(value = GlobalResourceRemovedMessage.class, name = "globalResourceRemoved"),})
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
