import type { EntityWithOrdinal } from '@/types/model/basic';
import type { ComputedRef } from 'vue';

export interface DraggableSupport {
  onDragEnd(event: DragEndEvent): Promise<void>;
}

export interface DragEndEvent {
  oldIndex: number;
  newIndex: number;
}

export function useDraggableSupport(list: ComputedRef<EntityWithOrdinal[]>,
                                    reorder: (input: EntityWithOrdinal[]) => Promise<void>
): DraggableSupport {
  async function onDragEnd(event: DragEndEvent): Promise<void> {
    if (event.newIndex === event.oldIndex) return;

    // TODO fix bug

    const list2: EntityWithOrdinal[] = [...list.value];
    const changed: EntityWithOrdinal[] = [];

    list2[event.oldIndex].ordinal = event.newIndex + 1;
    changed.push({ id: list2[event.oldIndex].id, ordinal: event.newIndex + 1 });

    if (event.newIndex > event.oldIndex) {
      // entity was moved down
      for (let i = event.oldIndex + 1; i <= event.newIndex; i++) {
        list2[i].ordinal = i;
        changed.push({ id: list2[i].id, ordinal: i });
      }
    } else {
      // entity was moved up
      for (let i = event.newIndex; i < event.oldIndex; i++) {
        list2[i].ordinal = i + 2;
        changed.push({ id: list2[i].id, ordinal: i + 2 });
      }
    }

    await reorder(changed);
  }

  return {
    onDragEnd
  };
}