package NlpInterface.rules;

import NlpInterface.schema.GraphPath;
import NlpInterface.schema.GraphSchema;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SynonymJson {
    public static JSONObject JsonObj = null;
    public static GraphSchema graphSchema;
    public static Map<String,Set<String>> nodedict = new HashMap<>();
    public static Map<String,Set<String>> edgedict = new HashMap<>();
    static {
        readJson();
    }
    public static void readJson(){
        String lines = "";
        try {
            lines = FileUtils.readFileToString(new File(NlpInterface.rules.PathsJson.class.getResource("/").getPath() + "Synonym.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonObj = new JSONObject(lines);
        for (int i = 0; i < JsonObj.getJSONArray("node").length(); i++){
            JSONObject obj = JsonObj.getJSONArray("node").getJSONObject(i);
            JSONArray nodeArr = obj.getJSONArray("nodeName");
            JSONArray simArr = obj.getJSONArray("similar");
            for (int j = 0; j <  nodeArr.length(); i++){
                for (int k = 0; k <  simArr.length(); k++){
                    String nodeStr = nodeArr.getString(j);
                    String simStr = simArr.getString(k);
                    if (!nodedict.keySet().contains(simArr.getString(k))){
                        nodedict.put(simStr,new HashSet<>());
                    }
                    nodedict.get(simStr).add(nodeStr);
                }
            }
        }
        for (int i = 0; i < JsonObj.getJSONArray("relation").length(); i++){
            JSONObject obj = JsonObj.getJSONArray("relation").getJSONObject(i);
            JSONArray edgeArr = obj.getJSONArray("nodeName");
            JSONArray simArr = obj.getJSONArray("similar");
            for (int j = 0; j <  edgeArr.length(); i++){
                for (int k = 0; k <  simArr.length(); k++){
                    String edgeStr = edgeArr.getString(j);
                    String simStr = simArr.getString(k);
                    if (!edgedict.keySet().contains(simArr.getString(k))){
                        edgedict.put(simStr,new HashSet<>());
                    }
                    edgedict.get(simStr).add(edgeStr);
                }
            }
        }
    }


}
