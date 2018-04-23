package apps;

import NlpInterface.NLPInterpreter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

class SnowView {
    public static void main(String[] args) throws Exception {
        String query = "superClass of the Class";
        NLPInterpreter.pipeline(query);
        Server server = new Server(8081);
        WebAppContext ctx = new WebAppContext("src/main/webapp", "/SnowGraph");
        server.setHandler(ctx);
        try {
        server.start();
        server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}