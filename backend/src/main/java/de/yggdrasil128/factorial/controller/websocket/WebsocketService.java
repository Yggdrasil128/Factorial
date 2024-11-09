package de.yggdrasil128.factorial.controller.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.yggdrasil128.factorial.controller.websocket.messages.*;
import de.yggdrasil128.factorial.engine.ProductionLine;
import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.engine.ResourceContributions;
import de.yggdrasil128.factorial.model.EntityPosition;
import de.yggdrasil128.factorial.model.changelist.*;
import de.yggdrasil128.factorial.model.factory.*;
import de.yggdrasil128.factorial.model.game.GameRemovedEvent;
import de.yggdrasil128.factorial.model.game.GameStandalone;
import de.yggdrasil128.factorial.model.game.GameUpdatedEvent;
import de.yggdrasil128.factorial.model.game.GamesReorderedEvent;
import de.yggdrasil128.factorial.model.icon.IconRemovedEvent;
import de.yggdrasil128.factorial.model.icon.IconStandalone;
import de.yggdrasil128.factorial.model.icon.IconUpdatedEvent;
import de.yggdrasil128.factorial.model.item.ItemRemovedEvent;
import de.yggdrasil128.factorial.model.item.ItemStandalone;
import de.yggdrasil128.factorial.model.item.ItemUpdatedEvent;
import de.yggdrasil128.factorial.model.machine.MachineRemovedEvent;
import de.yggdrasil128.factorial.model.machine.MachineStandalone;
import de.yggdrasil128.factorial.model.machine.MachineUpdatedEvent;
import de.yggdrasil128.factorial.model.productionstep.*;
import de.yggdrasil128.factorial.model.recipe.RecipeRemovedEvent;
import de.yggdrasil128.factorial.model.recipe.RecipeStandalone;
import de.yggdrasil128.factorial.model.recipe.RecipeUpdatedEvent;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierRemovedEvent;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierStandalone;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierUpdatedEvent;
import de.yggdrasil128.factorial.model.resource.*;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveStandalone;
import de.yggdrasil128.factorial.model.save.SaveUpdatedEvent;
import de.yggdrasil128.factorial.model.save.SavesReorderedEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class WebsocketService extends TextWebSocketHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WebsocketService.class);

    private final FactoryService factoryService;
    private final ProductionStepService productionStepService;
    private final ChangelistService changelistService;

    private final ObjectMapper mapper;
    private final Set<WebSocketSession> sessions;
    private final AtomicInteger lastMessageIdCounter;
    private final String runtimeId;

    private final BlockingQueue<AbstractMessage> outboundMessages = new ArrayBlockingQueue<>(1000);
    private final Thread messageSender = new Thread(this::broadcastMessages, "WebSocketSender");

    public WebsocketService(FactoryService factoryService, ProductionStepService productionStepService,
                            ChangelistService changelistService) {
        this.factoryService = factoryService;
        this.productionStepService = productionStepService;
        this.changelistService = changelistService;

        mapper = new ObjectMapper();
        sessions = new CopyOnWriteArraySet<>();
        lastMessageIdCounter = new AtomicInteger(0);
        runtimeId = UUID.randomUUID().toString();
    }

    @PostConstruct
    public void startMessaging() {
        messageSender.start();
    }

    @PreDestroy
    public void stopMessaging() {
        messageSender.interrupt();
        try {
            messageSender.join();
        } catch (InterruptedException exc) {
            LOG.warn("Interrupted while joining outbound message thread");
        }
        for (WebSocketSession session : sessions) {
            try {
                session.close(CloseStatus.GOING_AWAY);
            } catch (IOException exc) {
                LOG.error("IOException while closing websocket connection, ID: {}, remote address: {}", session.getId(),
                        session.getRemoteAddress());
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        LOG.info("Established new websocket connection, ID: {}, remote address: {}", session.getId(),
                session.getRemoteAddress());

        InitialMessage message = new InitialMessage(runtimeId, lastMessageIdCounter.get());
        try {
            TextMessage textMessage = convertMessage(message);
            session.sendMessage(textMessage);
        } catch (IOException e) {
            LOG.warn("IOException while sending message to websocket, ID: {}, remote address: {}", session.getId(),
                    session.getRemoteAddress(), e);
        }

        sessions.add(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        LOG.info("Received message from websocket, ID: {}, remote address: {}, message: {}", session.getId(),
                session.getRemoteAddress(), message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        LOG.info("Encountered a transport error, ID: {}, remote address: {}", session.getId(),
                session.getRemoteAddress(), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        LOG.info("Websocket connection closed, ID: {}, remote address: {}", session.getId(),
                session.getRemoteAddress());
        sessions.remove(session);
    }

    private TextMessage convertMessage(AbstractMessage message) throws JsonProcessingException {
        String json = mapper.writeValueAsString(message);
        return new TextMessage(json);
    }

    public void broadcastMessages() {
        try {
            while (true) {
                AbstractMessage message = outboundMessages.take();
                try {
                    broadcast(convertMessage(message));
                } catch (JsonProcessingException e) {
                    LOG.error("Could not convert message for broadcasting, message: {}", message, e);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.debug("Outbound message thread was interrupt, expecting shutdown");
        }
    }

    private void broadcast(TextMessage message) {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(message);
            } catch (IOException e) {
                LOG.warn("IOException while sending message to websocket, ID: {}, remote address: {}", session.getId(),
                        session.getRemoteAddress(), e);
            }
        }
    }

    public void enqueue(AbstractMessage message) {
        try {
            outboundMessages.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("Interrupted while trying to enqueue message {}", message, e);
        }
    }

    @EventListener
    public void on(GamesReorderedEvent event) {
        List<EntityPosition> order = event.getGames().stream()
                .map(game -> new EntityPosition(game.getId(), game.getOrdinal())).toList();

        enqueue(new GamesReorderedMessage(runtimeId, nextMessageId(), order));
    }

    @EventListener
    public void on(GameUpdatedEvent event) {
        enqueue(new GameUpdatedMessage(runtimeId, nextMessageId(), event.getGame().getId(),
                GameStandalone.of(event.getGame())));
    }

    @EventListener
    public void on(GameRemovedEvent event) {
        enqueue(new GameRemovedMessage(runtimeId, nextMessageId(), event.getGameId()));
    }

    @EventListener
    public void on(IconUpdatedEvent event) {
        enqueue(new IconUpdatedMessage(runtimeId, nextMessageId(), event.getIcon().getGame().getId(),
                IconStandalone.of(event.getIcon())));
    }

    @EventListener
    public void on(IconRemovedEvent event) {
        enqueue(new IconRemovedMessage(runtimeId, nextMessageId(), event.getGameId(), event.getIconId()));
    }

    @EventListener
    public void on(ItemUpdatedEvent event) {
        enqueue(new ItemUpdatedMessage(runtimeId, nextMessageId(), event.getItem().getGame().getId(),
                ItemStandalone.of(event.getItem())));
    }

    @EventListener
    public void ON(ItemRemovedEvent event) {
        enqueue(new ItemRemovedMessage(runtimeId, nextMessageId(), event.getGameId(), event.getItemId()));
    }

    @EventListener
    public void on(RecipeUpdatedEvent event) {
        enqueue(new RecipeUpdatedMessage(runtimeId, nextMessageId(), event.getRecipe().getGame().getId(),
                RecipeStandalone.of(event.getRecipe())));
    }

    @EventListener
    public void on(RecipeRemovedEvent event) {
        enqueue(new RecipeRemovedMessage(runtimeId, nextMessageId(), event.getGameId(), event.getRecipeId()));
    }

    @EventListener
    public void on(RecipeModifierUpdatedEvent event) {
        enqueue(new RecipeModifierUpdatedMessage(runtimeId, nextMessageId(),
                event.getRecipeModifier().getGame().getId(), RecipeModifierStandalone.of(event.getRecipeModifier())));
    }

    @EventListener
    public void on(RecipeModifierRemovedEvent event) {
        enqueue(new RecipeModifierRemovedMessage(runtimeId, nextMessageId(), event.getGameId(),
                event.getRecipeModifierId()));
    }

    @EventListener
    public void on(MachineUpdatedEvent event) {
        enqueue(new MachineUpdatedMessage(runtimeId, nextMessageId(), event.getMachine().getGame().getId(),
                MachineStandalone.of(event.getMachine())));
    }

    @EventListener
    public void on(MachineRemovedEvent event) {
        enqueue(new MachineRemovedMessage(runtimeId, nextMessageId(), event.getGameId(), event.getMachineId()));
    }

    @EventListener
    public void on(SavesReorderedEvent event) {
        List<EntityPosition> order = event.getSaves().stream()
                .map(save -> new EntityPosition(save.getId(), save.getOrdinal())).toList();

        enqueue(new SavesReorderedMessage(runtimeId, nextMessageId(), order));
    }

    @EventListener
    public void on(SaveUpdatedEvent event) {
        Save save = event.getSave();

        enqueue(new SaveUpdatedMessage(runtimeId, nextMessageId(), save.getId(), SaveStandalone.of(save)));
    }

    @EventListener
    public void on(SaveRemovedMessage event) {
        enqueue(new SaveRemovedMessage(runtimeId, nextMessageId(), event.getSaveId()));
    }

    @EventListener
    public void on(FactoriesReorderedEvent event) {
        List<EntityPosition> order = event.getFactories().stream()
                .map(factory -> new EntityPosition(factory.getId(), factory.getOrdinal())).toList();

        enqueue(new FactoriesReorderedMessage(runtimeId, nextMessageId(), event.getSaveId(), order));
    }

    @EventListener
    public void on(FactoryUpdatedEvent event) {
        Factory factory = event.getFactory();
        Save save = factory.getSave();
        ProductionLine productionLine = event instanceof FactoryProductionLineChangedEvent
                ? ((FactoryProductionLineChangedEvent) event).getProductionLine()
                : factoryService.computeProductionLine(factory, changelistService::getProductionStepChanges);

        enqueue(new FactoryUpdatedMessage(runtimeId, nextMessageId(), save.getId(),
                FactoryStandalone.of(factory, productionLine)));
    }

    @EventListener
    public void on(FactoryRemovedEvent event) {
        enqueue(new FactoryRemovedMessage(runtimeId, nextMessageId(), event.getSaveId(), event.getFactoryId()));
    }

    @EventListener
    public void on(ProductionStepUpdatedEvent event) {
        ProductionStep productionStep = event.getProductionStep();
        Save save = productionStep.getFactory().getSave();
        ProductionStepThroughputs throughputs = event instanceof ProductionStepThroughputsChangedEvent
                ? ((ProductionStepThroughputsChangedEvent) event).getThroughputs()
                : productionStepService.computeThroughputs(productionStep,
                        () -> changelistService.getProductionStepChanges(productionStep));

        enqueue(new ProductionStepUpdatedMessage(runtimeId, nextMessageId(), save.getId(),
                ProductionStepStandalone.of(productionStep, throughputs)));
    }

    @EventListener
    public void on(ProductionStepRemovedEvent event) {
        enqueue(new ProductionStepRemovedMessage(runtimeId, nextMessageId(), event.getSaveId(),
                event.getProductionStepId()));
    }

    @EventListener
    public void on(ResourcesReorderedEvent event) {
        List<EntityPosition> order = event.getResources().stream()
                .map(resource -> new EntityPosition(resource.getId(), resource.getOrdinal())).toList();

        enqueue(new ResourcesReorderedMessage(runtimeId, nextMessageId(), event.getSaveId(), order));
    }

    @EventListener
    public void on(ResourceUpdatedEvent event) {
        if (event.isComplete()) {
            Resource resource = event.getResource();
            Factory factory = resource.getFactory();
            Save save = factory.getSave();
            ResourceContributions contributions = event instanceof ResourceContributionsChangedEvent
                    ? ((ResourceContributionsChangedEvent) event).getContributions()
                    : factoryService.computeProductionLine(factory, changelistService::getProductionStepChanges)
                            .getContributions(resource);

            enqueue(new ResourceUpdatedMessage(runtimeId, nextMessageId(), save.getId(),
                    ResourceStandalone.of(resource, contributions)));
        }
    }

    @EventListener
    public void on(ResourceRemovedEvent event) {
        enqueue(new ResourceRemovedMessage(runtimeId, nextMessageId(), event.getSaveId(), event.getResourceId()));
    }

    @EventListener
    public void on(ChangelistsReorderedEvent event) {
        List<EntityPosition> order = event.getChangelists().stream()
                .map(changelist -> new EntityPosition(changelist.getId(), changelist.getOrdinal())).toList();

        enqueue(new ChangelistsReorderedMessage(runtimeId, nextMessageId(), event.getSaveId(), order));
    }

    @EventListener
    public void on(ChangelistUpdatedEvent event) {
        enqueue(new ChangelistUpdatedMessage(runtimeId, nextMessageId(), event.getChangelist().getSave().getId(),
                ChangelistStandalone.of(event.getChangelist())));
    }

    @EventListener
    public void on(ChangelistRemovedEvent event) {
        enqueue(new ChangelistRemovedMessage(runtimeId, nextMessageId(), event.getSaveId(), event.getChangelistId()));
    }

    private int nextMessageId() {
        return lastMessageIdCounter.incrementAndGet();
    }
}
