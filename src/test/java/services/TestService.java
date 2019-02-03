package services;

import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.service.AccountService;
import com.revolut.service.TransactionService;
import com.revolut.service.UserService;

public abstract class TestService {

    protected static final int SUCCESSFUL_RESPONSE_CODE= 200;
    protected static final int ERROR_RESPONSE_CODE= 500;
    protected static final int NOT_FOUND_RESPONSE_CODE= 404;
    protected static final int BAD_REQUEST_RESPONSE_CODE= 400;

    protected static Server server = null;
    protected static PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

    protected static HttpClient client ;
    protected ObjectMapper mapper = new ObjectMapper();
    protected URIBuilder builder = new URIBuilder().setScheme("http").setHost("localhost:8084");


    @BeforeClass
    public static void setup() throws Exception {
        startServer();
        connManager.setDefaultMaxPerRoute(100);
        connManager.setMaxTotal(200);
        client= HttpClients.custom()
                .setConnectionManager(connManager)
                .setConnectionManagerShared(true)
                .build();

    }

    @AfterClass
    public static void closeClient() throws Exception {
        //server.stop();
        HttpClientUtils.closeQuietly(client);
    }


    private static void startServer() throws Exception {
        if (server == null) {
            server = new Server(8084);
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            server.setHandler(context);
            ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
            servletHolder.setInitParameter("jersey.config.server.provider.classnames",
                      UserService.class.getCanonicalName() + "," +
                            AccountService.class.getCanonicalName() + "," +
                         //   ServiceExceptionMapper.class.getCanonicalName() + "," +
                            TransactionService.class.getCanonicalName());
            server.start();
        }
    }
}
