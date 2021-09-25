package com.adobe.training.core.servlets;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import javax.servlet.ServletException;
import java.io.IOException;

@SlingServlet(paths = "/bin/title")
/* Use below code to apply based on ResourceType */
/*@SlingServlet(
        resourceTypes = "trainingproject/components/content/title",
        extensions = "html")*/
public class SampleTitleServlet extends SlingSafeMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Content-Type", "text/html");
        response.getWriter().print("<h1>Sling Servlet injected this title!</h1>");
        response.getWriter().close();
    }
}
