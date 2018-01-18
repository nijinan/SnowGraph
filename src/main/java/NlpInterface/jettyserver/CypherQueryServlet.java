package NlpInterface.jettyserver;

import NlpInterface.NLPInterpreter;
import NlpInterface.config.Config;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CypherQueryServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        String cypherStr = request.getParameter("query");
        String returnType = request.getParameter("returnType");
        Session session = Config.getNeo4jBoltDriver().session();
        String stat = cypherStr;
        System.out.println(cypherStr);
        StatementResult rs = session.run(stat);
        int i = 0;
        JSONArray nodesArray = new JSONArray();
        JSONArray relationshipsArray = new JSONArray();
        JSONObject graph = new JSONObject();

        JSONArray dataArray = new JSONArray();
        JSONArray resultsArray = new JSONArray();
        JSONObject returnResult = new JSONObject();
        Set<String> setStr = new HashSet<>();
        while (rs.hasNext()){
            i++;
            Record item = rs.next();
            if (returnType.equals("node")) {
                JSONObject obj = new JSONObject(item.get(0).asMap());
                obj.put("_id", item.get(1).asLong());
                obj.put("_labels", item.get(2).asList());
                nodesArray.put(obj);
            }else{
                setStr.add(item.get(0).asString());
            }
        }
        session.close();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        if (!returnType.equals("node")) {
            for (String s : setStr) nodesArray.put(s);
            response.getWriter().print(nodesArray.toString());
            return;
        }
        graph.put("nodes", nodesArray);
        graph.put("relationships", relationshipsArray);
        dataArray.put(new JSONObject().put("graph", graph));
        resultsArray.put(new JSONObject().put("data", dataArray));
        returnResult.put("results", resultsArray);
        JSONObject result = new JSONObject();
        result.put("searchResult" , returnResult);
        //result.put("index", index);  //注释原因同上
        //result.put("max" , resultLength);
        response.getWriter().print(result.toString());
    }
}