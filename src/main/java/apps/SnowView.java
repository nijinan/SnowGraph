package apps;

import NlpInterface.NLPInterpreter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

class SnowView {
    public static void main(String[] args) throws Exception {
        String query = "superClass of the Class that extends the Class call_method updatePendingMerges";
        NLPInterpreter.pipeline(query);
        Server server = new Server(8080);
        WebAppContext ctx = new WebAppContext("src/main/webapp", "/SnowGraph");
        server.setHandler(ctx);
        server.start();
        server.join();
    }
}