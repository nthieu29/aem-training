package com.adobe.training.core.schedulers;

import lombok.extern.slf4j.Slf4j;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Dictionary;

@Component(metatype = true, immediate = true, label = "Training Cleanup Scheduled Tasks")
@Service(Runnable.class)
@Property(name = "scheduler.expression", value = "*/20 * * * * ?") // Every 20 seconds
@Slf4j
public class CleanupScheduledTask implements Runnable {
    @Property(label = "Path", description = "Delete this path", value = "/mypathtraining")
    public static final String CLEANUP_PATH = "cleanupPath";
    @Reference
    private SlingRepository repository;
    private String cleanupPath;

    protected void activate(ComponentContext componentContext) {
        configure(componentContext.getProperties());
    }

    protected void configure(Dictionary<?, ?> properties) {
        this.cleanupPath = PropertiesUtil.toString(properties.get(CLEANUP_PATH), null);
        log.info("!!!configure: cleanupPath='{}''", this.cleanupPath);
    }

    @Override
    public void run() {
        log.info("!!!running now");
        Session session = null;
        try {
            session = repository.loginService("training", null);
            if (session.itemExists(cleanupPath)) {
                session.removeItem(cleanupPath);
                log.info("!!!node deleted");
                session.save();
            }
        } catch (RepositoryException e) {
            log.error("!!!exception during cleanup", e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }
}
