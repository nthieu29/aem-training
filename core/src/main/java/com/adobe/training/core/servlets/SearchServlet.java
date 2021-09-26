package com.adobe.training.core.servlets;

import com.day.cq.wcm.api.PageManager;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.servlet.ServletException;
import java.io.IOException;

/***
 * Example URI: http://localhost:4502/content/trainingproject/en.search.html?q=ipsum&wcmmode=disabled
 */
@SlingServlet(resourceTypes = "trainingproject/components/structure/page", selectors = "search")
public class SearchServlet extends SlingSafeMethodsServlet {
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Content-Type", "application/json");
        JSONObject jsonObject = new JSONObject();
        JSONArray resultArray = new JSONArray();

        try {
            //current node that is requested
            Node currentNode = request.getResource().adaptTo(Node.class);

            //cq:page node containing the requested node
            PageManager pageManager = request.getResource().getResourceResolver().adaptTo(PageManager.class);
            Node queryRoot = pageManager.getContainingPage(currentNode.getPath()).adaptTo(Node.class);

            String queryTerm = request.getParameter("q");
            if (queryTerm != null) {
                NodeIterator searchResults = performSearchWithSQL(queryRoot, queryTerm);
                ;
                if (searchResults != null) {
                    while (searchResults.hasNext()) resultArray.put(searchResults.nextNode().getPath());
                }
                jsonObject.put("results", resultArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.getWriter().print(jsonObject.toString());
        response.getWriter().close();
    }

    private NodeIterator performSearchWithSQL(Node queryRoot, String queryTerm) throws RepositoryException {
        QueryManager qm = queryRoot.getSession().getWorkspace().getQueryManager();
        Query query = qm.createQuery("SELECT * FROM [nt:unstructured] AS node WHERE ISDESCENDANTNODE(["
                + queryRoot.getPath() + "]) and CONTAINS(node.*, '" + queryTerm + "')", Query.JCR_SQL2);
        return query.execute().getNodes();
    }
}
