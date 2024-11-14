package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.QuantityByChangelist;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Represents the changes a {@link Changelist} makes to {@link ProductionStep ProductionSteps}.
 * <p>
 * Most of the time, instances of this class are linked (more or less) directly to the corresponding instances of
 * {@link ProductionStepThroughputs}, so that changelist updates can be propagated directly.
 * <p>
 * During our initial computation, however, we must assume that production steps have not computed their throughputs,
 * because that needs changelist information. Therefore, this implementation distinguishes between an unlinked and a
 * linked state. This way an initial computation can first compute all changelist information in its unlinked state,
 * then compute the production step throughputs and finally link it all together using
 * {@link #establishLink(ProductionStepThroughputs, boolean)}.
 */
public class ProductionStepChanges {

    private static final Logger LOG = LoggerFactory.getLogger(ProductionStepChanges.class);

    private int changelistId;
    private boolean primary;
    private boolean active;
    // key is ProductionStep.id, but we must not keep references to the entities here
    private Map<Integer, Link> links = new HashMap<>();

    public class Link {

        private final int productionStepId;
        private Fraction change;
        private ProductionStepThroughputs throughputs;

        Link(int productionStepId, Fraction change, ProductionStepThroughputs throughputs) {
            this.productionStepId = productionStepId;
            this.throughputs = throughputs;
            this.change = change;
        }

        public Fraction getChange() {
            return change;
        }

        public ProductionStepThroughputs getThroughputs() {
            return throughputs;
        }

        void setThroughputs(ProductionStepThroughputs throughputs) {
            this.throughputs = throughputs;
        }

        boolean applyChanges() {
            Fraction currentChange = Fraction.ZERO;
            Fraction primaryChange = primary ? change : Fraction.ZERO;
            Fraction activeChange = active ? change : Fraction.ZERO;
            return doChange(currentChange, primaryChange, activeChange);
        }

        boolean deactivatePrimary() {
            return doChange(Fraction.ZERO, change.negative(), Fraction.ZERO);
        }

        boolean setChange(Fraction value) {
            Fraction currentChange = Fraction.ZERO;
            Fraction primaryChange = primary ? value.subtract(change) : Fraction.ZERO;
            Fraction activeChange = active ? value.subtract(change) : Fraction.ZERO;
            change = value;
            return doChange(currentChange, primaryChange, activeChange);
        }

        public boolean applyChange() {
            boolean result = applyChange0();
            links.remove(productionStepId);
            return result;
        }

        private boolean applyChange0() {
            if (change.isZero()) {
                return false;
            }
            Fraction currentChange = change;
            Fraction primaryChange = primary ? Fraction.ZERO : change;
            Fraction activeChange = Fraction.ZERO;
            change = Fraction.ZERO;
            return doChange(currentChange, primaryChange, activeChange);
        }

        boolean undo() {
            if (change.isZero()) {
                return false;
            }
            Fraction currentChange = Fraction.ZERO;
            Fraction primaryChange = primary ? change.negative() : Fraction.ZERO;
            Fraction activeChange = active ? change.negative() : Fraction.ZERO;
            change = Fraction.ZERO;
            return doChange(currentChange, primaryChange, activeChange);
        }

        private boolean doChange(Fraction currentChange, Fraction primaryChange, Fraction activeChange) {
            if (null == throughputs) {
                return false;
            }
            QuantityByChangelist throughputsChange = new QuantityByChangelist(currentChange, primaryChange,
                    activeChange);
            if (throughputsChange.isZero()) {
                return false;
            }
            LOG.debug("Changing machine counts, Changelist: {}, Production Step: {}, change: {}", changelistId,
                    throughputs.getEntityId(), throughputsChange);
            throughputs.changeMachineCounts(throughputsChange);
            return true;
        }

        @Override
        public String toString() {
            return change
                    + (null == throughputs ? " (unlinked)" : " (linked to PID " + throughputs.getEntityId() + ")");
        }

    }

    @FunctionalInterface
    public interface ProdutionStepThroghputsCallback {

        ProductionStepThroughputs computeThroughputs(ProductionStep productionStep,
                                                     Supplier<? extends QuantityByChangelist> changes);

    }

    public ProductionStepChanges(Changelist changelist) {
        changelistId = changelist.getId();
        primary = changelist.isPrimary();
        active = changelist.isActive();
        for (Map.Entry<ProductionStep, Fraction> entry : changelist.getProductionStepChanges().entrySet()) {
            links.put(entry.getKey().getId(), new Link(entry.getKey().getId(), entry.getValue(), null));
        }
    }

    public Link getLink(int productionStepId) {
        return links.get(productionStepId);
    }

    public boolean establishLink(ProductionStepThroughputs throughputs, boolean applyChanges) {
        Link link = links.get(throughputs.getEntityId());
        if (null != link && null == link.getThroughputs()) {
            LOG.debug("Establishing link, Changelist: {}, Production Step: {}", changelistId,
                    throughputs.getEntityId());
            link.setThroughputs(throughputs);
            if (applyChanges) {
                return link.applyChanges();
            }
        }
        return false;
    }

    public QuantityByChangelist getChanges(ProductionStep productionStep) {
        Link link = links.get(productionStep.getId());
        if (null == link) {
            return QuantityByChangelist.ZERO;
        }
        Fraction currentChange = Fraction.ZERO;
        Fraction primaryChange = primary ? link.getChange() : Fraction.ZERO;
        Fraction activeChange = active ? link.getChange() : Fraction.ZERO;
        return new QuantityByChangelist(currentChange, primaryChange, activeChange);
    }

    public Collection<ProductionStepThroughputs> deactivatePrimary() {
        Collection<ProductionStepThroughputs> cache = new ArrayList<>(links.size());
        for (Link link : links.values()) {
            if (link.deactivatePrimary()) {
                cache.add(link.getThroughputs());
            }
        }
        primary = false;
        return cache;
    }

    public boolean setChange(int productionStepId, ProductionStepThroughputs throughputs, Fraction change) {
        Link link = links.compute(productionStepId, (key, existing) -> {
            if (null == existing) {
                return new Link(productionStepId, Fraction.ZERO, throughputs);
            }
            existing.setThroughputs(throughputs);
            return existing;
        });
        return link.setChange(change);
    }

    public Collection<ProductionStepThroughputs> undo() {
        Collection<ProductionStepThroughputs> cache = new ArrayList<>(links.size());
        for (Link link : links.values()) {
            if (link.undo()) {
                cache.add(link.getThroughputs());
            }
        }
        links.clear();
        return cache;
    }

    public boolean drop(int productionStepId) {
        return null != links.remove(productionStepId);
    }

    @Override
    public String toString() {
        return links.toString();
    }

}
