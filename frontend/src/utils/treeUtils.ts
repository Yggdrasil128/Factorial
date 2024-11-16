export interface EntityWithCategory {
  id: number;
  name: string;
  iconId?: number;
  category: string[];
}

export type TreeNode = {
  id?: number;
  key: string;
  iconId?: number;
  label: string;
  children?: TreeNode[];
  value?: number;
}

export function convertToTreeByCategory<T extends EntityWithCategory>(
  elements: T[],
  isIconEntity?: boolean,
  additionalCategories?: string[][],
): TreeNode[] {
  const tree: TreeNode[] = [];

  function insert(nodes: TreeNode[], element: T | undefined, categoryPath: string[], key: string) {
    if (categoryPath.length > 0) {
      const category: string = categoryPath.shift() as string;
      for (const node of nodes) {
        if (node.children && node.label === category) {
          insert(node.children, element, categoryPath, key + category + '/');
          return;
        }
      }
      const node: TreeNode = {
        key: key + category + '/',
        label: category,
        children: [],
      };
      nodes.push(node);
      insert(node.children as TreeNode[], element, categoryPath, key + category + '/');
      return;
    }
    if (!element) {
      return;
    }
    const node: TreeNode = {
      id: element.id,
      key: String(element.id),
      value: element.id,
      iconId: isIconEntity ? element.id : element.iconId,
      label: element.name,
    };
    nodes.push(node);
  }

  for (const element of elements) {
    insert(tree, element, [...element.category], '/');
  }

  if (additionalCategories) {
    for (const category of additionalCategories) {
      insert(tree, undefined, [...category], '/');
    }
  }

  function sort(nodes: TreeNode[]) {
    nodes.sort((a: TreeNode, b: TreeNode): number => {
      if (!a.children !== !b.children) {
        return !a.children ? 1 : -1;
      }
      return a.label.localeCompare(b.label);
    });

    for (const node of nodes) {
      if (node.children) {
        sort(node.children);
      }
    }
  }

  sort(tree);

  return tree;
}