package de.yggdrasil128.factorial.model.changelist;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.NamedModel;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record ChangelistStandalone(int id,
                                   @JsonProperty(access = READ_ONLY) int saveId,
                                   Integer ordinal,
                                   String name,
                                   Boolean primary,
                                   Boolean active,
                                   Object iconId,
                                   List<ProductionStepChangeStandalone> productionStepChanges) {

    public static ChangelistStandalone of(Changelist model) {
        return of(model, External.FRONTEND);
    }

    public static ChangelistStandalone of(Changelist model, External destination) {
        return new ChangelistStandalone(model.getId(), model.getSave().getId(), model.getOrdinal(), model.getName(),
                model.isPrimary(), model.isActive(), NamedModel.resolve(model.getIcon(), destination),
                model.getProductionStepChanges().entrySet().stream()
                        .map(entry -> ProductionStepChangeStandalone.of(entry, destination)).toList());
    }
}
