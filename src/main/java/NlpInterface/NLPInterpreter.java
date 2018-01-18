package NlpInterface;

import NlpInterface.entity.*;
import NlpInterface.entity.TokenMapping.NLPVertexMapping;
import NlpInterface.wrapper.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class NLPInterpreter {
    public static String cypherStr = "";
    public static Query ansQuery;
    public static List<Query> queries = new ArrayList<>();
    private static int offsetMax;
    public static JSONObject pipeline(String plainText){

        queries.clear();
        Query query = generatorTokens(plainText);
        mapTokensToNodeAndRelation(query);
        List<NLPToken> tmp = new ArrayList<>();
        offsetMax = query.tokens.size();
        for (NLPToken token : query.tokens){
            if (token.mapping != null){
                tmp.add(token);
            }
        }
        query.tokens = tmp;
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < offsetMax; i++) list.add(0);
        cypherStr  = "";
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();

        DFS(query,0,list,arr);
        //generatorTuples(query);
        //generatorTupleLinks(query);
        int tot = 0;
        List<Query> answers = new ArrayList<>();
        for (Query query1 : queries){
            if (query1.nodes.size() == 0) continue;
            List<Query> listq = new ArrayList<>();
            listq.addAll(LinkAllNodes.process(query1));
            for (Query q : listq){
                Evaluator.evaluate(q);
                if (q.score < -0.1) continue;
                generatorInferenceLinks(q);
                String s = generatorCyphers(q);
                q.cypher = s;
                if (!s.equals("")) {
                    cypherStr += s + "</br>";
                    q.rank = tot;
                    tot++;
                    //arr.put(q.toJsonQuery());
                    answers.add(q);
                }
            }
        }
        System.out.println(answers.size());
        answers.sort(Comparator.comparing(p->p.score));
        if (answers.size() > 20) answers = answers.subList(0,20);
        for (Query q : answers) {
            arr.put(q.toJsonQuery());
        }
        obj.put("rankedResults", arr);
        return obj;
        /*CypherSet cyphers = generatorCyphers(query);
        for (Cypher cypher : cyphers.sets){
            System.out.println(cypher.text);
        }*/
    }

    public static void DFS(Query query, int offset, List<Integer> list, JSONArray arr){
        if (offset == offsetMax){
            for (NLPToken token : query.tokens){
                if (list.get((int)token.offset) < 0) token.mapping = null; else
                token.mapping = token.mappingList.get(list.get((int)token.offset));
            }
            Query newquery = query.copyOut();
            mapToSchema(newquery);
            List<Query> querys = EdgeMappingSchema.process(newquery);
            queries.addAll(querys);
            return;
        }
        boolean flag = false;
        for (NLPToken token : query.tokens){
            if (token.offset == offset){
                flag = true;
                list.set(offset,-1);
                DFS(query,offset+1, list,arr);
                for (int i = 0; i < token.mappingList.size(); i++){
                    list.set(offset,i);
                    DFS(query,offset+1, list,arr);
                }
            }
        }

        if (!flag) DFS(query,offset+1, list,arr);
    }

    public static Query generatorTokens(String plainText){
        return TokensGenerator.generator(plainText);
    }


    public static void mapTokensToNodeAndRelation(Query query){
        TokenMapping.process(query);
    }

    public static void generatorInferenceLinks(Query query){
        InferenceLinksGenerator.generate(query);
    }

    public static void mapToSchema(Query query){
        SchemaMapping.mapping(query);
    }
    public static String generatorCyphers(Query query){
        return CyphersGenerator.generate(query);
    }
}

