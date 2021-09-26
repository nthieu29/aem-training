package com.adobe.training.core.listeners;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;

import java.util.HashMap;
import java.util.Map;

@Component(immediate = true)
@Service(JobConsumer.class)
@Property(name = JobConsumer.PROPERTY_TOPICS, value = "com/adobe/training/core/replicationjob")
@Slf4j
public class ReplicationLogger implements JobConsumer {
    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public JobResult process(Job job) {
        final String pagePath = job.getProperty("PAGE_PATH").toString();
        ResourceResolver resourceResolver = null;
        try {
            Map<String, Object> serviceParams = new HashMap<String, Object>();
            serviceParams.put(ResourceResolverFactory.SUBSERVICE, "training");
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(serviceParams);
        } catch (LoginException e) {
            e.printStackTrace();
        }
        final PageManager pm = resourceResolver.adaptTo(PageManager.class);
        final Page page = pm.getContainingPage(pagePath);
        if (page != null) {
            log.info("+++++++++++++ ACTIVATION OF PAGE : {}", page.getTitle());
        }
        return JobConsumer.JobResult.OK;
    }
}
