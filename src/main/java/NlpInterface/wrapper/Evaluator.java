package NlpInterface.wrapper;

import NlpInterface.entity.NLPNode;
import NlpInterface.entity.NLPRelation;
import NlpInterface.entity.NLPToken;
import NlpInterface.entity.Query;
import NlpInterface.entity.TokenMapping.NLPVertexSchemaMapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Evaluator {
    public static Query query;
    public static boolean visited[] = new boolean[100];
    public static Set<NLPNode> visitedNode = new HashSet<>();
    public static Set<NLPRelation> visitedRelation = new HashSet<>();
    public static void evaluate(Query query){
        Evaluator.query = query;
        if (!isLink()){
            query.score = -1;
            return;
        }
        double val = mappingNum() * 100 + offsetValue() + graphComplex() * 10 + similar() * 0.1;
        if (val < 4.2)
            System.out.println();
        query.score = val;
    }

    public static int mappingNum(){
        int tot = 0;
        for (NLPToken token : query.tokens){
            if (!token.nomapping) tot++;
            if (token.mapping != null) tot--;
        }
        return tot;
    }

    public static double dfsNode(NLPNode start, NLPNode node, int len){
        double tot = 0;
        visitedNode.add(node);
        List<NLPNode> allNodes = new ArrayList<>();
        List<NLPRelation> allRelations = new ArrayList<>();
        allNodes.addAll(node.nextNode);
        allNodes.addAll(node.lastNode);
        allRelations.addAll(node.nextRelation);
        allRelations.addAll(node.lastRelation);
        for (int i = 0; i < allNodes.size(); i++){
            NLPNode n = allNodes.get(i);
            if (visitedNode.contains(n)) continue;
            NLPRelation r = allRelations.get(i);
            visitedRelation.add(r);visitedRelation.add(r.mirror);
            double bias = 2.0;
            if (r.token != null){
                if (r.direct ^ !r.token.POS.equals("VBD") ^ (start.token.offset < r.token.offset)){
                    bias = 1.0;
                }
                tot += bias * Math.abs(start.token.offset - r.token.offset);
            }else if (!n.token.text.equals("what")){
                    tot += Math.abs(start.token.offset - n.token.offset)*(len+1) / 2 ;
                }
            else{
                tot += dfsNode(start,n,len+1);
            }

        }
        return tot;
    }

    public static double offsetValue(){
        double val = 0;
        for (NLPNode node : query.nodes){
            if (!node.token.text.equals("what")){
                visitedRelation.clear();
                visitedNode.clear();
                val += dfsNode(node,node,0);
            }else {
                for (int i = 0; i < node.nextNode.size(); i++) {
                    NLPNode n = node.nextNode.get(i);
                    NLPRelation r = node.nextRelation.get(i);
                    if (r.token != null){
                        if (n.token.text.equals("what")){

                        }
                    }
                }
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
            for (int i = 0; i < node.nextNode.size(); i++) {
                NLPNode n = node.nextNode.get(i);
                NLPRelation r = node.nextRelation.get(i);
                if (r.otherType != null && r.otherType.equals("hidden")){
                    val += 1;
                }
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
                nodeVal += node.token.mapping.rank;
                nodeNum ++;
            }
        }
        for (NLPNode node : query.nodes){
            for (int i = 0; i < node.nextNode.size(); i++) {
                NLPRelation r = node.nextRelation.get(i);
                if (r.token != null){
                    nodeVal += r.token.mapping.rank;
                    nodeNum ++;
                }
            }
        }
        return nodeVal / nodeNum;
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

