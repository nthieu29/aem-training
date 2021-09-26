package com.adobe.training.core.listeners;

import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import lombok.extern.slf4j.Slf4j;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import java.util.HashMap;
import java.util.Map;

@Component(immediate = true)
@Service(EventHandler.class)
@Property(name = EventConstants.EVENT_TOPIC, value = ReplicationAction.EVENT_TOPIC)
@Slf4j
public class ReplicationListener implements EventHandler {
    private static final String TOPIC = "com/adobe/training/core/replicationjob";
    @Reference
    private JobManager jobManager;

    @Override
    public void handleEvent(Event event) {
        ReplicationAction action = ReplicationAction.fromEvent(event);
        if (action.getType().equals(ReplicationActionType.ACTIVATE) && action.getPath() != null) {
            fireReplicationEvent(action);
            log.info("Replication event with path: " + action.getPath());
        }
    }

    private void fireReplicationEvent(ReplicationAction replicationAction) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("PAGE_PATH", replicationAction.getPath());
        jobManager.addJob(TOPIC, properties);
    }
}
