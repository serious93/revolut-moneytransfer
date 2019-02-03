package com.revolut;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import com.revolut.service.AccountService;
import com.revolut.service.TransactionService;
import com.revolut.service.UserService;

/**
 * Main Class (Starting point) 
 */
public class Application {

	public static void main(String[] args) throws Exception {
		startService();
	}

	private static void startService() throws Exception {
		Server server = new Server(8084);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);
		ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
		servletHolder.setInitParameter("jersey.config.server.provider.classnames",
					UserService.class.getCanonicalName() + ","
						+ AccountService.class.getCanonicalName() + ","
						+ TransactionService.class.getCanonicalName());
		server.start();
	}

}
