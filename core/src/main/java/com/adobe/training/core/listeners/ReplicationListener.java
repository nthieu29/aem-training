package com.adobe.training.core.listeners;

import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import lombok.extern.slf4j.Slf4j;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

@Component(immediate = true)
@Service(EventHandler.class)
@Property(name = EventConstants.EVENT_TOPIC, value = ReplicationAction.EVENT_TOPIC)
@Slf4j
public class ReplicationListener implements EventHandler {

    @Override
    public void handleEvent(Event event) {
        ReplicationAction action = ReplicationAction.fromEvent(event);
        if (action.getType().equals(ReplicationActionType.ACTIVATE) && action.getPath() != null) {
            log.info("Replication event with path: " + action.getPath());
        }
    }
}
