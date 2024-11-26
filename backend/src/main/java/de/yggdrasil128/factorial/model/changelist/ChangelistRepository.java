package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangelistRepository extends CrudRepository<Changelist, Integer> {

    boolean existsBySaveIdAndName(int saveId, String name);

    boolean existsByIdAndPrimaryIsTrue(int changelistId);

    Changelist findBySaveIdAndPrimaryIsTrue(int saveId);

    Changelist findBySaveIdAndIdNotAndPrimaryIsTrue(int saveId, int changelistId);

    Iterable<Changelist> findAllBySaveIdAndActiveIsTrue(int saveId);

    @Query("select c from Changelist c join c.productionStepChanges p where ( KEY(p) = :productionStep )")
    Iterable<Changelist> findAllByProductionStepChangesProductionStep(ProductionStep productionStep);
}
