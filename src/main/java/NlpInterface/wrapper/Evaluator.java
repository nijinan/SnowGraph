package NlpInterface.wrapper;

import NlpInterface.entity.NLPNode;
import NlpInterface.entity.NLPRelation;
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
        double val = offsetValue() + graphComplex() + similar();
        query.score = val;
    }
    public static double offsetValue(){
        double val = 0;
        for (NLPNode node : query.nodes){
            for (int i = 0; i < node.nextNode.size(); i++){
                NLPNode n = node.nextNode.get(i);
                NLPRelation r = node.nextRelation.get(i);
            }
        }
        return val;
    }

    public static double graphComplex(){
        double val = 0;
        for (NLPNode node : query.nodes){
            if (node.token.text.equals("what")){
                val += 1;
            }
        }
        return val;
    }

    public static double similar(){
        double val = 0;
        double nodeVal = 0;
        int nodeNum = 0;
        for (NLPNode node : query.nodes){
            if (!node.token.text.equals("what")){
                nodeVal += node.token.mapping.score;
                nodeNum ++;
            }
        }
        return val;
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
