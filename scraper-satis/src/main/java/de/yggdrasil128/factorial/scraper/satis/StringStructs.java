package de.yggdrasil128.factorial.scraper.satis;

import java.util.*;

import static java.util.stream.Collectors.joining;

public class StringStructs {

    private static enum NodeType {
        UNKNOWN, LIST, MAP
    }

    public static interface Node extends Iterable<Node> {

        default Node get(int index) {
            return null;
        }

        default Node get(String fieldName) {
            return null;
        }

        @Override
        default Iterator<Node> iterator() {
            return Collections.emptyIterator();
        }

        default String asText() {
            return null;
        }

    }

    private static class ListNode implements Node {

        private final List<Node> list = new ArrayList<>();

        ListNode() {
        }

        @Override
        public Node get(int index) {
            return list.get(index);
        }

        @Override
        public Iterator<Node> iterator() {
            return list.iterator();
        }

        @Override
        public String toString() {
            return list.stream().map(Object::toString).collect(joining(",", "(", ")"));
        }

    }

    private static class MapNode implements Node {

        private final Map<String, Node> map = new HashMap<>();

        MapNode() {
        }

        @Override
        public Node get(String fieldName) {
            return map.get(fieldName);
        }

        @Override
        public String toString() {
            return map.entrySet().stream().map(entry -> entry.getKey() + '=' + entry.getValue())
                    .collect(joining(",", "(", ")"));
        }

    }

    private static class TextNode implements Node {

        private final String text;

        TextNode(String value) {
            this.text = value;
        }

        @Override
        public String asText() {
            return text;
        }

        @Override
        public String toString() {
            return text;
        }

    }

    private enum EmptyNode implements Node {
        INSTANCE;
    }

    private static class ParsingStackFrame {

        ParsingStackFrame() {
        }

        NodeType nodeType = NodeType.UNKNOWN;
        Node node = null;
        Node childNode = null;
        StringBuilder keyBuffer = new StringBuilder();
        StringBuilder valueBuffer = new StringBuilder();
        StringBuilder buffer = keyBuffer;

    }

    public static Node parseStringStruct(String s) {
        if (s.isEmpty() || '(' != s.charAt(0) || ')' != s.charAt(s.length() - 1)) {
            return new TextNode(s);
        }
        Deque<ParsingStackFrame> stack = new ArrayDeque<>();
        boolean text = false;
        for (int i = 0; i < s.length(); i++) {
            /*
             * We are guaranteed to have a '(' at the first position, hence we will get a stack.push in the first
             * iteration, hence after that, this will never be null.
             */
            ParsingStackFrame frame = stack.peek();
            char c = s.charAt(i);
            switch (c) {
            case '"':
                text = !text;
                break;
            case '(':
                if (text) {
                    frame.buffer.append(c);
                } else {
                    stack.push(new ParsingStackFrame());
                }
                break;
            case ')':
                if (text) {
                    frame.buffer.append(c);
                } else {
                    switch (frame.nodeType) {
                    case UNKNOWN:
                        if (null == frame.childNode && frame.buffer.isEmpty()) {
                            return EmptyNode.INSTANCE;
                        }
                        frame.nodeType = NodeType.LIST;
                        frame.node = new ListNode();
                        //$FALL-THROUGH$
                    case LIST:
                        finishListEntry(frame);
                        break;
                    case MAP:
                        finishMapEntry(frame);
                        break;
                    default:
                        break;
                    }
                    stack.pop();
                    if (stack.isEmpty()) {
                        if (i + 1 < s.length()) {
                            throw new IllegalArgumentException("missing opening paranthesis");
                        }
                        return frame.node;
                    }
                    stack.peek().childNode = frame.node;
                }
                break;
            case ',':
                if (text) {
                    frame.buffer.append(c);
                } else {
                    switch (frame.nodeType) {
                    case UNKNOWN:
                        frame.nodeType = NodeType.LIST;
                        frame.node = new ListNode();
                        //$FALL-THROUGH$
                    case LIST:
                        finishListEntry(frame);
                        break;
                    case MAP:
                        finishMapEntry(frame);
                        break;
                    }
                }
                break;
            case '=':
                if (text) {
                    frame.buffer.append(c);
                } else {
                    switch (frame.nodeType) {
                    case UNKNOWN:
                        frame.nodeType = NodeType.MAP;
                        frame.node = new MapNode();
                        //$FALL-THROUGH$
                    case MAP:
                        frame.buffer = frame.valueBuffer;
                        break;
                    case LIST:
                    }
                }
                break;
            default:
                frame.buffer.append(c);
                break;
            }
        }
        throw new IllegalArgumentException("missing closing paranthesis");
    }

    private static void finishMapEntry(ParsingStackFrame frame) {
        ((MapNode) frame.node).map.put(frame.keyBuffer.toString(),
                Optional.ofNullable(frame.childNode).orElse(new TextNode(frame.valueBuffer.toString())));
        frame.keyBuffer.setLength(0);
        frame.valueBuffer.setLength(0);
        frame.buffer = frame.keyBuffer;
    }

    private static void finishListEntry(ParsingStackFrame frame) {
        ((ListNode) frame.node).list
                .add(Optional.ofNullable(frame.childNode).orElse(new TextNode(frame.keyBuffer.toString())));
        frame.keyBuffer.setLength(0);
    }

}
