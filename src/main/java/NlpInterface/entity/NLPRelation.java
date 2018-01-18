package NlpInterface.entity;

import NlpInterface.schema.GraphAttribute;
import NlpInterface.schema.GraphEdgeType;
import exps.extractmodel.Graph;

public class NLPRelation {
    public NLPToken token;
    public GraphEdgeType edgeType;
    public GraphAttribute attrType;
    public String otherType;
    public boolean hidden = false;
    public NLPRelation mirror;
    public boolean direct = true;
    public NLPRelation(){
    }
    public NLPRelation(String type){
        this.otherType = type;
    }
    public NLPRelation(GraphEdgeType type, String name){
        this.edgeType = type;
        this.otherType = name;
    }
    public NLPRelation(GraphEdgeType type, NLPToken token){
        this.edgeType = type;
        this.token = token;
    }
}
