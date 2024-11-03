package de.yggdrasil128.factorial.controller.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.yggdrasil128.factorial.controller.websocket.messages.*;
import de.yggdrasil128.factorial.engine.ProductionLine;
import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.engine.ResourceContributions;
import de.yggdrasil128.factorial.model.changelist.ChangelistRemovedEvent;
import de.yggdrasil128.factorial.model.changelist.ChangelistStandalone;
import de.yggdrasil128.factorial.model.changelist.ChangelistUpdatedEvent;
import de.yggdrasil128.factorial.model.factory.*;
import de.yggdrasil128.factorial.model.productionstep.*;
import de.yggdrasil128.factorial.model.resource.*;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveService;
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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class WebsocketService extends TextWebSocketHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WebsocketService.class);

    private final SaveService saveService;
    private final FactoryService factoryService;
    private final ProductionStepService productionStepService;

    private final ObjectMapper mapper;
    private final Set<WebSocketSession> sessions;
    private final AtomicInteger lastMessageIdCounter;
    private final String runtimeId;

    public WebsocketService(SaveService saveService, FactoryService factoryService,
                            ProductionStepService productionStepService) {
        this.saveService = saveService;
        this.factoryService = factoryService;
        this.productionStepService = productionStepService;

        mapper = new ObjectMapper();
        sessions = new CopyOnWriteArraySet<>();
        lastMessageIdCounter = new AtomicInteger(0);
        runtimeId = UUID.randomUUID().toString();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        LOG.info("Established new websocket connection, ID: {}, remote address: {}", session.getId(),
                session.getRemoteAddress());

        InitialMessage message = new InitialMessage(runtimeId, lastMessageIdCounter.get());
        TextMessage textMessage = convertMessage(message);
        try {
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

    public void broadcast(AbstractMessage message) {
        TextMessage textMessage = convertMessage(message);
        broadcast(textMessage);
    }

    private TextMessage convertMessage(AbstractMessage message) {
        try {
            String json = mapper.writeValueAsString(message);
            return new TextMessage(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void broadcast(TextMessage message) {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(message);
            } catch (IOException e) {
                LOG.warn("IOException while sending message to websocket, ID: {}, remote address: {}", session.getId(),
                        session.getRemoteAddress(), e);
            }
        }
    }

    @EventListener
    public void on(FactoryUpdatedEvent event) {
        Factory factory = event.getFactory();
        Save save = factory.getSave();
        ProductionLine productionLine = event instanceof FactoryProductionLineChangedEvent
                ? ((FactoryProductionLineChangedEvent) event).getProductionLine()
                : factoryService.computeProductionLine(factory, () -> saveService.computeProductionStepChanges(save));

        FactoryUpdatedMessage message = new FactoryUpdatedMessage(runtimeId, nextMessageId(), save.getId(),
                FactoryStandalone.of(factory, productionLine));
        broadcast(message);
    }

    @EventListener
    public void on(FactoryRemovedEvent event) {
        FactoryRemovedMessage message = new FactoryRemovedMessage(runtimeId, nextMessageId(), event.getSaveId(),
                event.getFactoryId());
        broadcast(message);
    }

    @EventListener
    public void on(ProductionStepUpdatedEvent event) {
        ProductionStep productionStep = event.getProductionStep();
        Save save = productionStep.getFactory().getSave();
        ProductionStepThroughputs throughputs = event instanceof ProductionStepThroughputsChangedEvent
                ? ((ProductionStepThroughputsChangedEvent) event).getThroughputs()
                : productionStepService.computeThroughputs(productionStep,
                        () -> saveService.computeProductionStepChanges(save));

        ProductionStepUpdatedMessage message = new ProductionStepUpdatedMessage(runtimeId, nextMessageId(),
                save.getId(), ProductionStepStandalone.of(productionStep, throughputs));
        broadcast(message);
    }

    @EventListener
    public void on(ProductionStepRemovedEvent event) {
        ProductionStepRemovedMessage message = new ProductionStepRemovedMessage(runtimeId, nextMessageId(),
                event.getSaveId(), event.getProductionStepId());
        broadcast(message);
    }

    @EventListener
    public void on(ResourceUpdatedEvent event) {
        Resource resource = event.getResource();
        Factory factory = resource.getFactory();
        Save save = factory.getSave();
        ResourceContributions contributions = event instanceof ResourceContributionsChangedEvent
                ? ((ResourceContributionsChangedEvent) event).getContributions()
                : factoryService.computeProductionLine(factory, () -> saveService.computeProductionStepChanges(save))
                        .getContributions(resource);

        ResourceUpdatedMessage message = new ResourceUpdatedMessage(runtimeId, nextMessageId(), save.getId(),
                ResourceStandalone.of(resource, contributions));
        broadcast(message);
    }

    @EventListener
    public void on(ResourceRemovedEvent event) {
        ResourceRemovedMessage message = new ResourceRemovedMessage(runtimeId, nextMessageId(), event.getSaveId(),
                event.getResourceId());
        broadcast(message);
    }

    @EventListener
    public void on(ChangelistUpdatedEvent event) {
        ChangelistUpdatedMessage message = new ChangelistUpdatedMessage(runtimeId, nextMessageId(),
                event.getChangelist().getSave().getId(), ChangelistStandalone.of(event.getChangelist()));
        broadcast(message);
    }

    @EventListener
    public void on(ChangelistRemovedEvent event) {
        ChangelistRemovedMessage message = new ChangelistRemovedMessage(runtimeId, nextMessageId(), event.getSaveId(),
                event.getChangelistId());
        broadcast(message);
    }

    private int nextMessageId() {
        return lastMessageIdCounter.incrementAndGet();
    }
}
