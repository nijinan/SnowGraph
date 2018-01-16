package NlpInterface.jettyserver;

import NlpInterface.NLPInterpreter;
import NlpInterface.config.Config;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CypherQueryServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        String cypherStr = request.getParameter("query");
        Session session = Config.getNeo4jBoltDriver().session();
        String stat = cypherStr;
        StatementResult rs = session.run(stat);
        int i = 0;
        while (rs.hasNext()){
            i++;
            rs.next();
        }
        System.out.println(i);
        response.setContentType("application/json");
        response.getWriter().print(NLPInterpreter.pipeline(cypherStr));
    }
}