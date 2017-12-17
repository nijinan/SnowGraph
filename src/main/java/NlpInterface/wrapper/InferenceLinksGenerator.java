package NlpInterface.wrapper;

import NlpInterface.entity.*;
import NlpInterface.entity.TokenMapping.NLPVertexSchemaMapping;

import java.util.HashSet;
import java.util.Set;

public class InferenceLinksGenerator {
    public static NLPNode startNode;
    public static Query query;
    public static Set<Object> visited = new HashSet<>();
    public static void generate(Query query){
        /*从多个tuples链接成一个link 按照顺序*/
        InferenceLinksGenerator.query = query;
        init();
        findStart();
        if (query.focusNode == null) return;
        NLPInferenceLink link = new NLPInferenceLink();
        query.inferenceLinks.add(link);
        link.start = new NLPInferenceNode(startNode);
        findLink(link.start);
    }
    public static void init(){
        int cnt = 0;
        for (NLPNode node : query.nodes){
            if (node.token.mapping instanceof NLPVertexSchemaMapping)
                node.id = cnt++;
        }
        for (NLPNode node : query.nodes){
            if (!(node.token.mapping instanceof NLPVertexSchemaMapping))
                node.id = cnt++;
        }
    }
    public static void findStart(){
        for (NLPNode node : query.nodes){
            if (node.focus){
                query.focusNode = node;
                startNode = node;
                return;
            }
        }
        for (NLPNode node : query.nodes){
            if (node.token.mapping instanceof NLPVertexSchemaMapping){
                query.focusNode = node;
                startNode = node;
                return;
            }
        }
    }
    public static void findLink(NLPInferenceNode _inferenceNode){
        NLPInferenceNode inferenceNode = _inferenceNode;
        NLPNode startNode = inferenceNode.node;
        visited.add(startNode);
        boolean flag = false;
        for (int i = 0; i < startNode.nextNode.size(); i++){
            NLPNode node = startNode.nextNode.get(i);
            NLPRelation relation = startNode.nextRelation.get(i);
            if (relation.edgeType == null) continue;
            if (visited.contains(relation) || visited.contains(relation.mirror)) continue;
            if (flag){
                NLPInferenceLink link = new NLPInferenceLink();
                inferenceNode = new NLPInferenceNode(startNode);
                link.start = inferenceNode;
                query.inferenceLinks.add(link);
            }
            inferenceNode.nextInferNode = new NLPInferenceNode(node);
            inferenceNode.nextRelation = relation;
            visited.add(relation);
            visited.add(relation.mirror);
            inferenceNode.direct = true;
            flag = true;
            if (visited.contains(node)) {
                inferenceNode.isEnd = true;
                continue;
            }
            findLink(inferenceNode.nextInferNode);
        }
        for (int i = 0; i < startNode.lastNode.size(); i++){
            NLPNode node = startNode.lastNode.get(i);
            NLPRelation relation = startNode.lastRelation.get(i);
            if (visited.contains(relation) || visited.contains(relation.mirror)) continue;
            if (flag){
                NLPInferenceLink link = new NLPInferenceLink();
                inferenceNode = new NLPInferenceNode(startNode);
                link.start = inferenceNode;
                query.inferenceLinks.add(link);
            }
            inferenceNode.nextInferNode = new NLPInferenceNode(node);
            inferenceNode.nextRelation = relation;
            visited.add(relation);
            visited.add(relation.mirror);
            inferenceNode.direct = false;
            flag = true;
            if (visited.contains(node)) {
                inferenceNode.isEnd = true;
                continue;
            }
            findLink(inferenceNode.nextInferNode);
        }
        if (!flag) inferenceNode.isEnd = true;
    }
}
