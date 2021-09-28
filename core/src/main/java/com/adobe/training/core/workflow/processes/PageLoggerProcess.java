package com.adobe.training.core.workflow.processes;

import com.day.cq.wcm.api.Page;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;

/***
 * Note:
 * 1. Create a Workflow model in AEM Tools/Workflow/Models
 * 2. Create a Workflow launchers in AEM Tools/Workflow/Launchers
 */

@Component
@Service
@Slf4j
@Properties({@Property(name = Constants.SERVICE_DESCRIPTION, value = "This workflow will log page info."),
        @Property(name = Constants.SERVICE_VENDOR, value = "nthieu29"),
        @Property(name = "process.label", value = "Page Logger Process")})
public class PageLoggerProcess implements WorkflowProcess {
    private static final String TYPE_JCR_PATH = "JCR_PATH";
    private static final String TYPE_JCR_UUID = "JCR_UUID";
    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        // get the node the workflow is acting on
        Session session = workflowSession.getSession();

        //allows us to get the resourceResolver based on the current session
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user.jcr.session", session);
        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceResolverFactory.getResourceResolver(params);
        } catch (LoginException e) {
            log.error("@@@@@LoginError" + e);
        }
        if (resourceResolver != null) {
            try {
                Resource resource = getChangedResource(workItem, session, resourceResolver);
                Page page = resource.adaptTo(Page.class);
                log.info("Page Logger with title: " + page.getTitle());
                log.info("Page Logger with page path: " + page.getPath());
            } catch (RepositoryException e) {
                log.error("@@@@@RepositoryException", e);
            }
        } else {
            log.error("@@@@@ResourceResolver is null");
        }
    }

    private Resource getChangedResource(WorkItem workItem, Session session, ResourceResolver resourceResolver) throws RepositoryException {
        WorkflowData data = workItem.getWorkflowData();
        Node payloadNode = null;
        String type = data.getPayloadType();
        //make sure the payload is a valid jcr path
        if (type.equals(TYPE_JCR_PATH) && data.getPayload() != null) {
            String payloadData = (String) data.getPayload();
            if (session.itemExists(payloadData)) {
                payloadNode = session.getNode(payloadData);
            }
        } else if (data.getPayload() != null && type.equals(TYPE_JCR_UUID)) {
            payloadNode = session.getNodeByIdentifier((String) data.getPayload());
        }
        return resourceResolver.getResource(payloadNode.getPath());
    }
}
