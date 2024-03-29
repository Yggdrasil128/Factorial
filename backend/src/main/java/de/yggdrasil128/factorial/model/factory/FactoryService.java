package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.ReorderInputEntry;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveRepository;
import de.yggdrasil128.factorial.model.xgress.Xgress;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

@Service
public class FactoryService extends ModelService<Factory, FactoryRepository> {

    private final IconService icons;
    private final SaveRepository saves;

    public FactoryService(FactoryRepository repository, IconService icons, SaveRepository saves) {
        super(repository);
        this.icons = icons;
        this.saves = saves;
    }

    public Factory create(Save save, FactoryInput input) {
        int ordinal = 0 == input.getOrdinal()
                ? save.getFactories().stream().mapToInt(Factory::getOrdinal).max().orElse(0) + 1
                : input.getOrdinal();
        Icon icon = OptionalInputField.ofId(input.getIconId(), icons::get).get();
        return repository.save(new Factory(save, ordinal, input.getName(), input.getDescription(), icon, emptyList(),
                emptyList(), emptyList(), emptyMap()));
    }

    public void addAttachedProductionStep(Factory factory, ProductionStep productionStep) {
        factory.getProductionSteps().add(productionStep);
        initItemOrder(factory, productionStep);
        repository.save(factory);
    }

    public void addAttachedIngress(Factory factory, Xgress ingress) {
        factory.getIngresses().add(ingress);
        repository.save(factory);
    }

    public void addAttachedEgress(Factory factory, Xgress egress) {
        factory.getEgresses().add(egress);
        repository.save(factory);
    }

    private static void initItemOrder(Factory factory, ProductionStep productionStep) {
        for (Resource resource : productionStep.getRecipe().getInput()) {
            factory.getItemOrder().computeIfAbsent(resource.getItem(), key -> factory.getItemOrder().size() + 1);
        }
        for (Resource resource : productionStep.getRecipe().getOutput()) {
            factory.getItemOrder().computeIfAbsent(resource.getItem(), key -> factory.getItemOrder().size() + 1);
        }
    }

    public Factory update(int id, FactoryInput input) {
        Factory factory = get(id);
        OptionalInputField.of(input.getName()).apply(factory::setName);
        OptionalInputField.of(input.getDescription()).apply(factory::setDescription);
        OptionalInputField.ofId(input.getIconId(), icons::get).apply(factory::setIcon);
        return repository.save(factory);
    }

    public void reorder(Save save, List<ReorderInputEntry> input) {
        Map<Integer, Integer> order = input.stream()
                .collect(toMap(ReorderInputEntry::getId, ReorderInputEntry::getOrdinal));
        for (Factory factory : save.getFactories()) {
            Integer ordinal = order.get(factory.getId());
            if (null != ordinal) {
                factory.setOrdinal(ordinal.intValue());
                repository.save(factory);
            }
        }
    }

    public void reorderItems(int id, List<ReorderInputEntry> input) {
        Factory factory = get(id);
        Map<Integer, Integer> order = input.stream()
                .collect(toMap(ReorderInputEntry::getId, ReorderInputEntry::getOrdinal));
        boolean modified = false;
        for (Map.Entry<Item, Integer> entry : factory.getItemOrder().entrySet()) {
            Integer ordinal = order.get(entry.getKey().getId());
            if (null != ordinal) {
                entry.setValue(ordinal);
                modified = true;
            }
        }
        if (modified) {
            repository.save(factory);
        }
    }

    @Override
    public void delete(int id) {
        Save save = saves.findByFactoriesId(id).orElse(null);
        if (null != save && 1 == repository.countBySaveId(save.getId())) {
            throw report(HttpStatus.CONFLICT, "cannot delete the last factory of a save");
        }
        repository.deleteById(id);
    }

}
