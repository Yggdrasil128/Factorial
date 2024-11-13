package de.yggdrasil128.factorial.model.save;

import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.NamedModel;

public record SaveStandalone(int id, Object gameId, Integer ordinal, String name, String description, Object iconId) {

    public static SaveStandalone of(Save model) {
        return of(model, External.FRONTEND);
    }

    public static SaveStandalone of(Save model, External destination) {
        return new SaveStandalone(model.getId(), NamedModel.resolve(model.getGame(), destination), model.getOrdinal(),
                model.getName(), model.getDescription(), NamedModel.resolve(model.getIcon(), destination));
    }

}
