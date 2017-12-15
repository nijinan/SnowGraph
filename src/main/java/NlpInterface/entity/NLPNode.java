package NlpInterface.entity;

import NlpInterface.schema.GraphVertexType;

import java.util.ArrayList;
import java.util.List;

public class NLPNode {
    public NLPToken token;
    public List<NLPNode> nextNode = new ArrayList<>();
    public List<NLPRelation> nextRelation = new ArrayList<>();
    public List<NLPNode> lastNode = new ArrayList<>();
    public List<NLPRelation> lastRelation = new ArrayList<>();
    public long id;
    public boolean focus = false;
    public NLPNode (NLPToken token){
        this.token = token;
    }
    public void addNext(NLPNode node, NLPRelation relation){
        nextNode.add(node);
        nextRelation.add(relation);
    }
    public void addLast(NLPNode node, NLPRelation relation){
        lastNode.add(node);
        lastRelation.add(relation);
    }
}
