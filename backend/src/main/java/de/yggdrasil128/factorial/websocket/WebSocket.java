package de.yggdrasil128.factorial.websocket;

import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.engine.ResourceContributions;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.save.Save;

public interface WebSocket {

    public void saveUpdated(int saveId, Save save);

    public void saveDeleted(int saveId);

    public void factoryUpdated(int saveId, Factory factory);

    public void factoryDeleted(int saveId, int factoryId);

    public void productionStepUpdated(int saveId, ProductionStep productionStep, ProductionStepThroughputs throughputs);

    public void productionStepDeleted(int saveId, int productionStepId);

    public void resourceUpdated(int saveId, Resource resource, ResourceContributions contributions);

    public void resourceDeleted(int saveId, int resourceId);

    public void changelistUpdated(int saveId, Changelist changelist);

    public void changelistDeleted(int saveId, int changelistId);

    class Placeholder implements WebSocket {

        @Override
        public void saveUpdated(int saveId, Save save) {
            System.out.println("save updated: " + save);
        }

        @Override
        public void saveDeleted(int saveId) {
            System.out.println("save deleted: " + saveId);
        }

        @Override
        public void factoryUpdated(int saveId, Factory factory) {
            System.out.println("factory updated: " + factory);
        }

        @Override
        public void factoryDeleted(int saveId, int factoryId) {
            System.out.println("factory deleted: " + factoryId);
        }

        @Override
        public void productionStepUpdated(int saveId, ProductionStep productionStep,
                                          ProductionStepThroughputs throughputs) {
            System.out.println("productionStep updated: " + productionStep + ": " + throughputs);
        }

        @Override
        public void productionStepDeleted(int saveId, int productionStepId) {
            System.out.println("productionStep deleted: " + productionStepId);
        }

        @Override
        public void resourceUpdated(int saveId, Resource resource, ResourceContributions contributions) {
            System.out.println("resource updated: " + resource + ": " + contributions);
        }

        @Override
        public void resourceDeleted(int saveId, int resourceId) {
            System.out.println("resource deleted: " + resourceId);
        }

        @Override
        public void changelistUpdated(int saveId, Changelist changelist) {
            System.out.println("changelist updated: " + changelist);
        }

        @Override
        public void changelistDeleted(int saveId, int changelistId) {
            System.out.println("changelist deleted: " + changelistId);
        }
    }

}
