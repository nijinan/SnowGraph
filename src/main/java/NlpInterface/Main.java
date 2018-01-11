package NlpInterface;

import NlpInterface.jettyserver.JettyServer;
import apps.Config;
import graphdb.extractors.parsers.stackoverflow.StackOverflowExtractor;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String args[]) {
        //String query = "superClass of the Class that extends IndexWriter";
        //String query = "superClass of the Class that extends the Class have_method doAfterFlush";
        //String query = "soAuthor of StackOverFlowQuestion that apiNameMention IndexWriter";
        String query = "superClass of IndexWriter";
        //String query = "superClass of the Class that extends the Class call_method updatePendingMerges";
        NLPInterpreter.pipeline(query);
//        Session session = Config.getNeo4jBoltDriver().session();
//        String stat = "MATCH (n1:Class)<-[extend]-(n2:Class)-[have_method]->(n0:Method) WHERE (n0.name = \"doAfterFlush\") RETURN n1.superClass";
//        StatementResult rs = session.run(stat);
//        int i = 0;
//        while (rs.hasNext()){
//            i++;
//            rs.next();
//        }
//        System.out.println(i);
        JettyServer.run();
    }
}
