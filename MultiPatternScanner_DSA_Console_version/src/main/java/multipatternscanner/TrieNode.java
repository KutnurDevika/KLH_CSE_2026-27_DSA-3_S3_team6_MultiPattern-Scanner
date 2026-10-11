package multipatternscanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrieNode {
    private final Map<Integer, TrieNode> children = new HashMap<>();
    private TrieNode failureLink;
    private final List<Integer> outputPatternIds = new ArrayList<>();

    public TrieNode getChild(int codePoint) {
        return children.get(codePoint);
    }

    public void addChild(int codePoint, TrieNode node) {
        children.put(codePoint, node);
    }

    public Map<Integer, TrieNode> getChildren() {
        return children;
    }

    public TrieNode getFailureLink() {
        return failureLink;
    }

    public void setFailureLink(TrieNode failureLink) {
        this.failureLink = failureLink;
    }

    public List<Integer> getOutputPatternIds() {
        return outputPatternIds;
    }

    public void addOutputPatternId(int id) {
        outputPatternIds.add(id);
    }
}
