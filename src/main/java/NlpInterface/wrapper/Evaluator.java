package NlpInterface.wrapper;

import NlpInterface.entity.NLPNode;
import NlpInterface.entity.Query;
import NlpInterface.entity.TokenMapping.NLPVertexSchemaMapping;

public class Evaluator {
    public static Query query;
    public static boolean visited[] = new boolean[100];
    public static void evaluate(Query query){
        Evaluator.query = query;
        if (!isLink()){
            query.score = -1;
            return;
        }
    }
    public static boolean isLink(){
        for (int i = 0; i < query.nodes.size(); i++) visited[i] = false;
        for (NLPNode node : query.nodes){
            if (node.token.mapping instanceof NLPVertexSchemaMapping){
                visit(node);
                break;
            }
        }

        for (int i = 0; i < query.nodes.size(); i++) {
            if (!visited[i]) return false;
        }
        return true;
    }
    public static void visit(NLPNode node){
        visited[node.id] = true;
        for (NLPNode nextNode : node.nextNode){
            if (!visited[nextNode.id]) visit(nextNode);
        }
        for (NLPNode lastNode : node.lastNode){
            if (!visited[lastNode.id]) visit(lastNode);
        }
    }

}
