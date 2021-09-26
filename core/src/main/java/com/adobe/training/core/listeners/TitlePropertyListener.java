package com.adobe.training.core.listeners;

import lombok.extern.slf4j.Slf4j;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;

import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

@Component
@Slf4j
public class TitlePropertyListener implements EventListener {
    @Reference
    private SlingRepository repository;

    private Session session;
    private ObservationManager observationManager;

    @Activate
    protected void activate(ComponentContext context) throws Exception {
        session = repository.loginService("training", null);
        observationManager = session.getWorkspace().getObservationManager();

        observationManager.addEventListener(
                this,
                Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED,
                "/content/trainingproject/fr",
                true,
                null,
                new String[]{"cq:PageContent", "nt:unstructured"},
                true);
        log.info("*************added JCR event listener");
    }

    @Deactivate
    protected void deactivate(ComponentContext componentContext) {
        try {
            if (observationManager != null) {
                observationManager.removeEventListener(this);
                log.info("*************removed JCR event listener");
            }
        } catch (RepositoryException re) {
            log.error("*************error removing the JCR event listener ", re);
        } finally {
            if (session != null) {
                session.logout();
                session = null;
            }
        }
    }

    @Override
    public void onEvent(EventIterator it) {
        while (it.hasNext()) {
            Event event = it.nextEvent();
            try {
                Property changedProperty = session.getProperty(event.getPath());
                if (changedProperty.getName().equalsIgnoreCase("jcr:title")
                        && !changedProperty.getString().endsWith("!")) {
                    changedProperty.setValue(changedProperty.getString() + "!");
                    log.info("*************Property updated: {}", event.getPath());
                    session.save();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
