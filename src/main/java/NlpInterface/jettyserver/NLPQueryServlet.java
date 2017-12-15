package NlpInterface.jettyserver;

import NlpInterface.NLPInterpreter;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NLPQueryServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        String cypherStr = request.getParameter("query");
        response.setContentType("text/plain");
        response.getWriter().print("asdouhgruihtg");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        String cypherStr = request.getParameter("query");
        response.setContentType("text/plain");
        response.getWriter().print(NLPInterpreter.pipeline(cypherStr));
    }
}