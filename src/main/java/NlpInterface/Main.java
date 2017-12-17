package NlpInterface;

import NlpInterface.jettyserver.JettyServer;
import graphdb.extractors.parsers.stackoverflow.StackOverflowExtractor;
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
        //String query = "superClass of IndexWriter";
        String query = "mail whose mailSender is  Dave Kor";
        NLPInterpreter.pipeline(query);
        JettyServer.run();
    }
}
