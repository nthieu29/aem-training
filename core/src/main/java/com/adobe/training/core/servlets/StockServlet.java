package com.adobe.training.core.servlets;

import com.adobe.training.core.models.StockModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Example URI http://localhost:4502/content/trainingproject/en.model.html/content/ADBE
 * <p>
 * To use this servlet, a content structure must be created:
 * /content
 * + ADBE [cq:Page]
 * + lastTrade [nt:unstructured]
 * - lastTrade = "100"
 * - request	Date = "11/13/2016"
 * - requestTime = "4:00pm"
 */

@SlingServlet(
        resourceTypes = "trainingproject/components/structure/page",
        selectors = "model",
        methods = "GET"
)
@Slf4j
public class StockServlet extends SlingAllMethodsServlet {
    private ResourceResolver resourceResolver;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        try {
            response.setContentType("text/html");
            String suffix = request.getRequestPathInfo().getSuffix();
            if (suffix == null) {
                response.getWriter().println("Can't get the last trade node, enter a suffix in the URI");
            } else {
                resourceResolver = request.getResourceResolver();
                Resource suffixResource = resourceResolver.getResource(suffix);
                Resource lastTradeNode = suffixResource.getChild("lastTrade");
                // Get lastTradeNode properties by using ValueMap
                ValueMap propertiesOfLastTradeNode = lastTradeNode.getValueMap();
                response.getOutputStream().println("<h3>");
                response.getOutputStream().println("lastTrade node with ValueMap is");
                response.getOutputStream().println("</h3><br />");
                response.getOutputStream().println("(Last Trade) " + propertiesOfLastTradeNode.get("lastTrade").toString() + " (Requested Time) " + propertiesOfLastTradeNode.get("requestDate").toString() + " " + propertiesOfLastTradeNode.get("requestTime").toString());

                // Get lastTradeNode properties by adapting to StockModel
                StockModel stockModel = suffixResource.adaptTo(StockModel.class);
                response.getOutputStream().println("<br /><h3>");
                response.getOutputStream().println("lastTrade node with StockModel is");
                response.getOutputStream().println("</h3><br />");
                response.getOutputStream().println("(Last Trade) " + stockModel.getLastTrade() + " (Requested Time) " + stockModel.getTimestamp());
            }
        } catch (Exception e) {
            response.getWriter().println("Can't read last trade node. make sure the suffix path exists!");
            log.error(e.getMessage());
        } finally {
            response.getWriter().close();
        }
    }
}
