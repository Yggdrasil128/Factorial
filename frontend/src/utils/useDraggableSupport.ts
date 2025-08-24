import type {EntityWithOrdinal} from '@/types/model/basic';
import type {ComputedRef} from 'vue';

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

    let newList: EntityWithOrdinal[];
    if (event.newIndex < event.oldIndex) {
      newList = [
        ...list.value.slice(0, event.newIndex),
        list.value[event.oldIndex],
        ...list.value.slice(event.newIndex, event.oldIndex),
        ...list.value.slice(event.oldIndex + 1),
      ];
    } else {
      newList = [
        ...list.value.slice(0, event.oldIndex),
        ...list.value.slice(event.oldIndex + 1, event.newIndex + 1),
        list.value[event.oldIndex],
        ...list.value.slice(event.newIndex + 1),
      ];
    }

    const changed: EntityWithOrdinal[] = [];

    for (let i: number = 0; i < newList.length; i++) {
      if (newList[i].ordinal !== i + 1) {
        newList[i].ordinal = i + 1;
        changed.push({ id: newList[i].id, ordinal: i + 1 });
      }
    }

    await reorder(changed);
  }

  return {
    onDragEnd
  };
}