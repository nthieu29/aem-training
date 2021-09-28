package com.adobe.training.core.workflow.processes;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

@Component
@Service
@Slf4j
@Properties({@Property(name = Constants.SERVICE_DESCRIPTION, value = "This workflow will add an attribute approved to the node."),
        @Property(name = Constants.SERVICE_VENDOR, value = "nthieu29"),
        @Property(name = "process.label", value = "Custom Approval Process")})
public class CustomApprovalProcess implements WorkflowProcess {
    private static final String TYPE_JCR_PATH = "JCR_PATH";

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        WorkflowData workflowData = workItem.getWorkflowData();
        if (workflowData.getPayloadType().equals(TYPE_JCR_PATH)) {
            String path = workflowData.getPayload().toString() + "/jcr:content";
            try {
                Node node = (Node) workflowSession.getSession().getItem(path);
                if (node != null) {
                    node.setProperty("approved", readArgument(metaDataMap));
                    workflowSession.getSession().save();
                }
            } catch (RepositoryException e) {
                throw new WorkflowException(e.getMessage(), e);
            }
        }
    }

    private boolean readArgument(MetaDataMap args) {
        String argument = args.get("PROCESS_ARGS", "false");
        return argument.equalsIgnoreCase("true");
    }
}
