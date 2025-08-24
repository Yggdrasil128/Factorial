import {reactive, type Ref, ref} from 'vue';
import {ElLoading} from 'element-plus';
import {isInitialMessage, type WebsocketMessage} from '@/types/websocketMessages/modelChangedMessages';

export interface ModelSyncWebsocket {
  connect: () => void;
  disconnect: () => void;
}

interface ElLoadingInstance {
  close: () => void;
}

const websocketUrl: string = '/websocket';
const websocketReconnectMillis: number = 2000;
const missedMessageTimeoutMillis: number = 3000;

export function useModelSyncWebsocket(
  messageHook: (message: WebsocketMessage) => void,
  reloadHook: () => void
): ModelSyncWebsocket {

  const socket: Ref<WebSocket | null> = ref(null);
  const hasConnectedOnce: Ref<boolean> = ref(false);
  const elLoading: Ref<ElLoadingInstance | null> = ref(null);
  const runtimeId: Ref<string> = ref('');
  const messageId: Ref<number> = ref(0);
  const messageBuffer: Map<number, WebsocketMessage> = reactive(new Map());
  const missedMessageTimeout: Ref<NodeJS.Timeout | null> = ref(null);

  function connect(): void {
    socket.value = new WebSocket(websocketUrl);

    socket.value.onopen = onWebsocketOpen;
    socket.value.onclose = onWebsocketClose;
    socket.value.onerror = onWebsocketClose;
    socket.value.onmessage = onWebsocketMessage;

    messageBuffer.clear();
  }

  function disconnect(): void {
    if (!socket.value) {
      return;
    }

    socket.value.onopen = null;
    socket.value.onclose = null;
    socket.value.onerror = null;
    socket.value.onmessage = null;

    socket.value.close();

    socket.value = null;
  }

  function onWebsocketOpen(): void {
    hasConnectedOnce.value = true;

    if (elLoading.value) {
      elLoading.value.close();
      elLoading.value = null;

      reloadHook();
    }
  }

  function onWebsocketClose(): void {
    if (!socket.value) {
      return;
    }

    socket.value.onopen = null;
    socket.value.onclose = null;
    socket.value.onerror = null;
    socket.value.onmessage = null;

    socket.value = null;

    if (!elLoading.value) {
      elLoading.value = ElLoading.service({
        lock: true,
        text: hasConnectedOnce.value ? 'Connection lost.\nAttempting to reconnect...' : 'Connecting...',
        background: 'rgba(0, 0, 0, 0.7)'
      });
    }

    setTimeout(connect, websocketReconnectMillis);
  }

  function onWebsocketMessage(event: MessageEvent): void {
    const message: WebsocketMessage = JSON.parse(event.data);

    if (isInitialMessage(message)) {
      messageId.value = message.messageId;

      if (runtimeId.value && runtimeId.value !== message.runtimeId) {
        // runtimeId has changed
        runtimeId.value = message.runtimeId;
        reloadHook();
      }
      return;
    }

    if (message.messageId <= messageId.value) {
      return;
    }

    if (message.messageId > messageId.value + 1) {
      // We missed a message
      messageBuffer.set(message.messageId, message);
      if (!missedMessageTimeout.value) {
        missedMessageTimeout.value = setTimeout(onMissedMessageTimedOut, missedMessageTimeoutMillis);
      }
      return;
    }

    // This message is the next one we need.
    messageHook(message);
    messageId.value = message.messageId;

    let oldMessage: WebsocketMessage | undefined = messageBuffer.get(messageId.value + 1);
    while (oldMessage) {
      messageBuffer.delete(oldMessage.messageId);
      messageHook(oldMessage);
      messageId.value = oldMessage.messageId;

      oldMessage = messageBuffer.get(messageId.value + 1);
    }

    if (messageBuffer.size === 0 && missedMessageTimeout.value) {
      clearTimeout(missedMessageTimeout.value);
      missedMessageTimeout.value = null;
    }
  }

  function onMissedMessageTimedOut(): void {
    missedMessageTimeout.value = null;

    reloadHook();
    if (socket.value) socket.value.close();
  }

  return {
    connect,
    disconnect
  };
}