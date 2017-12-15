package NlpInterface.jettyserver;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

public class JettyServer {
    public static final Server server = new Server(8080);
    public static final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    public static void run(){
        context.setContextPath("/");
        ServletHandler handler = new ServletHandler();
        ServletHolder sh = new ServletHolder(new NLPQueryServlet());
        context.addServlet(sh, "/NLPQuery");

        FilterHolder fh = handler.addFilterWithMapping(SimpleCORSFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        fh.setInitParameter("initParamKey", "InitParamValue");

        context.addFilter(fh, "/*", EnumSet.of(DispatcherType.REQUEST));
        server.setHandler(context);

        // http://localhost:8080/NLPQuery

        try {
            JettyServer.server.start();
            JettyServer.server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

