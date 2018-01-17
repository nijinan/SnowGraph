package NlpInterface.wrapper;

import NlpInterface.entity.*;
import NlpInterface.entity.TokenMapping.NLPAttributeSchemaMapping;
import NlpInterface.entity.TokenMapping.NLPMapping;
import NlpInterface.entity.TokenMapping.NLPVertexSchemaMapping;
import NlpInterface.schema.GraphVertexType;
import NlpInterface.schema.GraphPath;

import java.util.ArrayList;
import java.util.List;

public class LinkAllNodes {
    public static Query query;
    public static List<Query> queries;
    public static int color[] = new int[100];
    public static double dis[][] = new double[100][100];
    public static int disi[][] = new int[100][100];
    public static int disj[][] = new int[100][100];
    public static double prim[] = new double[100];
    public static int primi[] = new int[100];
    public static int primj[] = new int[100];
    public static int colors = 0;
    public static List<Query> process(Query query){
        LinkAllNodes.query = query;
        queries = new ArrayList<>();
        for (int i = 0; i < 100; i++) color[i] = -1;
        for (int i = 0; i < 100; i++)
            for (int j = 0; j < 100; j++)
                dis[i][j] = 1e10;
        colors = 0;
        coloring();
        MST();
        for (int i = 0; i < colors; i++) if(prim[i] > 1e9){
            return queries;
        }
        linking();

        queries.add(query);
        return queries;
    }
    public static void coloring(){
        for (int i = 0; i < query.nodes.size(); i++){
            if (color[query.nodes.get(i).id] < 0){
                visit(query.nodes.get(i),colors);
                colors++;
            }
        }
    }
    public static void visit(NLPNode node, int c){
        color[node.id] = c;
        for (NLPNode nextNode : node.nextNode){
            if (color[nextNode.id] < 0) visit(nextNode,c);
        }
        for (NLPNode lastNode : node.lastNode){
            if (color[lastNode.id] < 0) visit(lastNode,c);
        }
    }

    public static double getAverageOffset(NLPNode node){
        List<NLPNode> allNodes = new ArrayList<>();
        List<NLPRelation> allRelations = new ArrayList<>();
        allNodes.addAll(node.nextNode);
        allNodes.addAll(node.lastNode);
        allRelations.addAll(node.nextRelation);
        allRelations.addAll(node.lastRelation);
        double offset = 0;
        int tmp = 0;
        for (int i = 0; i < allNodes.size(); i++){
            if (allNodes.get(i).token.offset >= -0.1){
                tmp ++;
                offset += allNodes.get(i).token.offset;
            }
            if (allRelations.get(i).token != null){
                tmp ++;
                offset += allRelations.get(i).token.offset;
            }
        }
        offset /= tmp;
        return offset;
    }

    public static double distance(NLPNode node1, NLPNode node2){
        if (node1.token.mapping instanceof NLPAttributeSchemaMapping) return 1e10;
        if (node2.token.mapping instanceof NLPAttributeSchemaMapping) return 1e10;
        if (node1.token.mapping instanceof NLPVertexSchemaMapping && node2.token.mapping instanceof NLPVertexSchemaMapping){
            if (((NLPVertexSchemaMapping) node1.token.mapping).vertexType.shortestPaths.keySet().contains(((NLPVertexSchemaMapping) node2.token.mapping).vertexType.name)){
                int step = ((NLPVertexSchemaMapping) node1.token.mapping).vertexType.shortestPaths.get(((NLPVertexSchemaMapping) node2.token.mapping).vertexType.name).edges.size();
                double offset1 = node1.token.offset;
                double offset2 = node2.token.offset;
                if (offset1 < 0){
                    offset1 = getAverageOffset(node1);
                }
                if (offset2 < 0){
                    offset2 = getAverageOffset(node2);
                }
                return step * 100 + Math.abs(offset1 - offset2);

            }
        }
        return 1e10;
    }
    public static void MST(){
        //distance between color
        for (int i = 0; i < colors; i++){
            dis[i][i] = 0;
            for (int j = 0; j < colors; j++)if (i!=j){
                for (int idi = 0; idi < query.nodes.size(); idi++)if (color[query.nodes.get(idi).id]==i){
                    for (int idj = 0; idj < query.nodes.size(); idj++)if (color[query.nodes.get(idj).id]==j){
                        double tmp = distance(query.nodes.get(idi),query.nodes.get(idj));
                        if (tmp < dis[i][j]) {
                            dis[i][j] = tmp;
                            disi[i][j] = idi;
                            disj[i][j] = idj;
                            dis[j][i] = tmp;
                            disi[j][i] = idi;
                            disj[j][i] = idj;
                        }
                    }
                }
            }
        }
        //MST
        for (int i = 0; i < colors; i++) prim[i] = 1e10;
        prim[0] = 0;
        int x = 0;
        for (int i = 0; i < colors-1; i++){
            for (int j = 0; j < colors; j++){
                if (prim[j] > dis[x][j]) {
                    prim[j] = dis[x][j];
                    primi[j] = disi[x][j];
                    primj[j] = disj[x][j];
                }
            }
            x = 0;
            for (int j = 0; j < colors; j++){
                if (prim[j] > 0.1 && prim[j] < prim[x]) {
                    x = j;
                }
            }
            prim[x] = 0;
        }
    }
    public static void linking(){
        for (int i = 1; i < colors; i++){
            NLPNode nodei = query.nodes.get(primi[i]);
            NLPNode nodej = query.nodes.get(primj[i]);
            GraphPath path = ((NLPVertexSchemaMapping)nodei.token.mapping).vertexType.shortestPaths.get(((NLPVertexSchemaMapping)nodej.token.mapping).vertexType.name);
            NLPNode last = nodei;
            for (GraphVertexType vertexType : path.nodes){
                NLPRelation relation1 = new NLPRelation("hidden");
                NLPRelation relation2 = new NLPRelation("hidden");
                relation1.mirror = relation2;
                relation2.mirror = relation1;
                relation2.direct = false;
                NLPNode newNode = new NLPNode(new NLPToken("what"));
                NLPMapping mapping = new NLPVertexSchemaMapping(vertexType,newNode.token,1);
                newNode.token.mapping = mapping;
                newNode.id = query.nodes.size();
                query.nodes.add(newNode);
                last.addNext(newNode,relation1);
                newNode.addLast(last,relation2);
                last = newNode;
            }
            NLPRelation relation1 = new NLPRelation("hidden");
            NLPRelation relation2 = new NLPRelation("hidden");
            relation1.mirror = relation2;
            relation2.mirror = relation1;
            relation2.direct = false;
            last.addNext(nodej, relation1);
            nodej.addLast(last, relation2);
        }
    }


}
