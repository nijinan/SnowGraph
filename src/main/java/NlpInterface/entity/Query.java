package NlpInterface.entity;

import java.util.HashSet;
import java.util.Set;

public class Query {
    public String text;
    public Set<NLPToken> tokens = new HashSet<>();
    public Set<NLPInferenceLink> inferenceLinks = new HashSet<>();
    public Set<NLPNode> nodes = new HashSet<>();
    public NLPNode focusNode = null;
    public Query copy(){
        Query newQuery = new Query();
        newQuery.tokens = this.tokens;
        newQuery.text = this.text;
        return newQuery;
    }
}
