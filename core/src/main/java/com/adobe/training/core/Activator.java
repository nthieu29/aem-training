package com.adobe.training.core;

import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

@Slf4j
public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        log.info("### Bundle Started ###");
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        log.info("### Bundle Stopped ###");
    }
}
