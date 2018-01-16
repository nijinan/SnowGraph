package NlpInterface.entity;

import NlpInterface.schema.GraphAttribute;
import NlpInterface.schema.GraphEdgeType;
import exps.extractmodel.Graph;

public class NLPRelation {
    public NLPToken token;
    public GraphEdgeType edgeType;
    public GraphAttribute attrType;
    public String otherType;
    public NLPRelation mirror;
    public boolean direct = true;
    public NLPRelation(){
    }
    public NLPRelation(String type){
        this.otherType = type;
    }
    public NLPRelation(GraphEdgeType type){
        this.edgeType = type;
    }
}
