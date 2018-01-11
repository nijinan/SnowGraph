package NlpInterface.entity.TokenMapping;

import NlpInterface.entity.NLPToken;
import NlpInterface.schema.GraphEdgeType;
import NlpInterface.schema.GraphPath;

public class NLPPathSchemaMapping extends NLPMapping{
    public String type;
    public GraphPath path;
    public NLPPathSchemaMapping(String type, GraphPath edgeType, NLPToken token, double similar){
        super(token, similar);
        this.type = type;
        this.path = edgeType;
    }
}
