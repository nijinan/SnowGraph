package NlpInterface.entity;

import java.util.HashSet;
import java.util.Set;

public class Query {
    public String text;
    public Set<NLPToken> tokens = new HashSet<>();
    public Set<NLPTuple> tuples;
    public Set<NLPInferenceLink> inferenceLinks = new HashSet<>();
    public Set<NLPNode> nodes = new HashSet<>();
    public NLPNode focusNode;
    public long inferenceId;
    public static void main(String args[]){

    }
}
