package multipatternscanner;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class AhoCorasick {
    private final TrieNode root = new TrieNode();
    private final List<String> patterns = new ArrayList<>();

    public void insertPattern(String pattern) {
        int patternId = patterns.size();
        patterns.add(pattern);

        TrieNode current = root;

        for (int codePoint : pattern.codePoints().toArray()) {
            TrieNode next = current.getChild(codePoint);

            if (next == null) {
                next = new TrieNode();
                current.addChild(codePoint, next);
            }

            current = next;
        }

        current.addOutputPatternId(patternId);
    }

    public void buildFailureLinks() {
        Deque<TrieNode> queue = new ArrayDeque<>();
        root.setFailureLink(root);

        for (TrieNode child : root.getChildren().values()) {
            child.setFailureLink(root);
            queue.add(child);
        }

        while (!queue.isEmpty()) {
            TrieNode current = queue.remove();

            for (var entry : current.getChildren().entrySet()) {
                int symbol = entry.getKey();
                TrieNode child = entry.getValue();

                TrieNode failure = current.getFailureLink();

                while (failure != root && failure.getChild(symbol) == null) {
                    failure = failure.getFailureLink();
                }

                TrieNode fallback = failure.getChild(symbol);

                if (fallback != null && fallback != child) {
                    child.setFailureLink(fallback);
                } else {
                    child.setFailureLink(root);
                }

                child.getOutputPatternIds()
                     .addAll(child.getFailureLink().getOutputPatternIds());

                queue.add(child);
            }
        }
    }

    public List<MatchResult> search(String text) {
        List<MatchResult> results = new ArrayList<>();
        TrieNode current = root;

        int charIndex = 0;

        for (int offset = 0; offset < text.length();) {
            int codePoint = text.codePointAt(offset);
            int charCount = Character.charCount(codePoint);

            while (current != root && current.getChild(codePoint) == null) {
                current = current.getFailureLink();
            }

            TrieNode next = current.getChild(codePoint);
            current = (next != null) ? next : root;

            for (int patternId : current.getOutputPatternIds()) {
                String pattern = patterns.get(patternId);

                int end = charIndex + charCount - 1;
                int start = end - pattern.length() + 1;

                results.add(new MatchResult(pattern, start, end));
            }

            offset += charCount;
            charIndex += charCount;
        }

        return results;
    }

    public List<String> getPatterns() {
        return List.copyOf(patterns);
    }
}
