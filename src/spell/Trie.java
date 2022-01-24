package spell;
import java.util.Objects;
import java.util.SortedSet;

public class Trie implements spell.ITrie {
    private Node root;
    private Node currentNode;
    private int nodeCount;
    private int wordCount;
    private int freqCount;

    public Trie() {
        this.root = new Node();
        this.currentNode = root;
        nodeCount = 1;
    }

    @Override
    public void add(String word) {
        currentNode = root;
        for (int i = 0; i < word.length(); ++i){
            int charNum = word.charAt(i) - 'a';
            if(currentNode.getChildren()[charNum] == null){
                currentNode.getChildren()[charNum] = new Node();
                nodeCount++;
            }
            currentNode = (Node)currentNode.getChildren()[charNum]; // remember typecasting!
        }
        if(currentNode.getValue() < 1){
            wordCount++;
        }
        currentNode.incrementValue();
        freqCount++;
    }



    @Override
    public INode find(String word) {

        int charNum = word.charAt(0) - 'a';
        if(root.getChildren()[charNum] == null){
            return null;
        }
        else{
            currentNode = root;
            for (int j = 0; j < word.length(); j++){
                charNum = word.charAt(j) - 'a';
                if (currentNode.getChildren()[charNum] != null){
                    currentNode = (Node)currentNode.getChildren()[charNum];
                }
                else return null;
            }
            if (currentNode.getValue() > 0){
                return currentNode;
            }
        }
        return null;
    }

    @Override
    public int getWordCount() {
        return wordCount;
    }

    @Override
    public int getNodeCount() {
        return nodeCount;
    }


    @Override
    public boolean equals(Object obj){
        if(obj == null){ // is obj null?
            return false;
        }
        if(obj == this){ // is obj this?
            return true;
        }
        if (getClass() != obj.getClass()){ // is obj trie?
            return false;
        }
        Trie other = (Trie) obj;
        return ((wordCount == other.getWordCount()) && nodeCount == other.getNodeCount() && equalsHelper(this.root, other.root));
    }

    private boolean equalsHelper(INode thisNode, INode otherNode) {
        if (thisNode.getValue() != otherNode.getValue()) {
            return false;
        }

        for (int i = 0; i < thisNode.getChildren().length; ++i) {
            if (thisNode.getChildren()[i] == null && otherNode.getChildren()[i] != null ||
                    thisNode.getChildren()[i] != null && otherNode.getChildren()[i] == null) {
                return false;
            }
        }

        for (int i = 0; i < thisNode.getChildren().length; ++i) {
            if (thisNode.getChildren()[i] != null && otherNode.getChildren()[i] != null) {
                INode node1 = thisNode.getChildren()[i];
                INode node2 = otherNode.getChildren()[i];
                if (!equalsHelper(node1, node2)){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < root.getChildren().length; i++){
            if (root.getChildren()[i] != null){
                hash += (i*29);
            }
        }
        return  nodeCount * wordCount + hash;
    }

    @Override
    public String toString(){
        StringBuilder currentWord = new StringBuilder();
        StringBuilder output = new StringBuilder();
        toStringHelper(root, currentWord, output);

        return output.toString();
    }

    private void toStringHelper(Node node, StringBuilder currentWord, StringBuilder output){
        if (node.getValue() > 0) {
            output.append(currentWord.toString() + "\n");
        }
        for (int i = 0; i < node.getChildren().length; ++i){
            Node child = (Node)node.getChildren()[i];
            if(child != null){
                char childChar = (char) ('a' + i);

                currentWord.append(childChar);

                toStringHelper(child,currentWord,output);

                currentWord.deleteCharAt(currentWord.length() - 1);
            }
        }
    }
}

