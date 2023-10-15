package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.save.Save;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static java.util.Collections.emptyMap;

@Service
public class ChangelistService extends ModelService<Changelist, ChangelistRepository> {

    private final IconService icons;

    public ChangelistService(ChangelistRepository repository, IconService icons) {
        super(repository);
        this.icons = icons;
    }

    public Changelist create(Save save, ChangeListInput input) {
        int ordinal = 0 == input.getOrdinal()
                ? save.getChangelists().stream().mapToInt(Changelist::getOrdinal).max().orElse(0) + 1
                : input.getOrdinal();
        Icon icon = 0 == input.getIconId() ? null : icons.get(input.getIconId());

        Changelist result = repository.save(
                new Changelist(save, ordinal, input.getName(), input.isPrimary(), input.isActive(), icon, emptyMap()));
        if (result.isPrimary()) {
            Changelist primary = getPrimaryChangelist(save);
            primary.setPrimary(false);
            repository.save(primary);
        }
        return result;
    }

    public Changelist update(int id, ChangeListInput input) {
        Changelist changelist = get(id);
        if (null != input.getName()) {
            changelist.setName(input.getName());
        }
        if (0 != input.getIconId()) {
            changelist.setIcon(icons.get(input.getIconId()));
        }
        return repository.save(changelist);
    }

    public void reportMachineCount(Changelist changelist, ProductionStep productionStep, Fraction machineCount) {
        Fraction delta = machineCount.subtract(productionStep.getMachineCount());
        if (Fraction.ZERO.equals(delta)) {
            changelist.getProductionStepChanges().remove(productionStep);
        } else {
            changelist.getProductionStepChanges().put(productionStep, delta);
        }
        repository.save(changelist);
    }

    @Override
    public void delete(int id) {
        if (repository.existsByIdAndPrimaryIsTrue(id)) {
            throw report(HttpStatus.CONFLICT, "cannot delete primary changelist");
        }
        super.delete(id);
    }

    public void setPrimary(int id) {
        Changelist changelist = get(id);
        if (!changelist.isPrimary()) {
            Changelist primary = getPrimaryChangelist(changelist.getSave());
            changelist.setPrimary(true);
            changelist.setActive(true);
            repository.save(changelist);
            primary.setPrimary(false);
            repository.save(primary);
        }
    }

    public void setActive(int id, boolean active) {
        Changelist changelist = get(id);
        if (!changelist.isPrimary()) {
            if (changelist.isActive() != active) {
                changelist.setActive(active);
                repository.save(changelist);
            }
        } else if (!active) {
            throw report(HttpStatus.CONFLICT, "cannot set primary changelist to inactive");
        }
    }

    public static Changelist getPrimaryChangelist(Save save) {
        return save.getChangelists().stream().filter(Changelist::isPrimary).findAny()
                .orElseThrow(ChangelistService::reportPrimaryChangelistNotAvailable);
    }

    private static ResponseStatusException reportPrimaryChangelistNotAvailable() {
        return ModelService.report(HttpStatus.INTERNAL_SERVER_ERROR,
                "assertion failed: no primary changelist available");
    }

}
