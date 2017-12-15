package NlpInterface.entity.TokenMapping;

import NlpInterface.entity.NLPToken;
import NlpInterface.schema.GraphEdgeType;

public class NLPEdgeSchemaMapping extends NLPMapping{
    public String type;
    public GraphEdgeType edgeType;
    public NLPEdgeSchemaMapping(String type, GraphEdgeType edgeType, NLPToken token, double similar){
        super(token, similar);
        this.type = type;
        this.edgeType = edgeType;
    }
}
