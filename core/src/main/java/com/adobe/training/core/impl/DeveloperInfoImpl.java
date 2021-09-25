package com.adobe.training.core.impl;

import com.adobe.training.core.DeveloperInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.commons.osgi.PropertiesUtil;

import java.util.Arrays;
import java.util.Map;

@Component(metatype = true, label = "Training Developer Info")
@Service(value = DeveloperInfo.class)
@Slf4j
public class DeveloperInfoImpl implements DeveloperInfo {
    /* Define configurable properties for this component */
    @Property(label = "Show Info", description = "Should the Developer information be shown?", boolValue = false)
    public static final String PROPERTY_SHOW_INFO = "show.info";

    @Property(label = "Developer name", description = "Name of the Developer")
    public static final String PROPERTY_DEVELOPER_NAME = "name";

    @Property(label = "Hobbies", description = "List your favorite Hobbies", unbounded = PropertyUnbounded.ARRAY)
    public static final String PROPERTY_HOBBIES = "hobbies";

    @Property(label = "Programming Language", description = "Programming language of developer", options = {
            @PropertyOption(name = "Java", value = "Java"),
            @PropertyOption(name = "Javascript", value = "Javascript"),
            @PropertyOption(name = "Python", value = "Python"),
    })
    public static final String PROPERTY_PROGRAMMING_LANGUAGE = "language";

    /* Local variables */
    private boolean showDeveloperInfo;
    private String developerName;
    private String[] developerHobbies;
    private String developerLanguage;

    @Override
    public String getDeveloperInfo() {
        if (showDeveloperInfo) {
            return "Created by " + developerName
                    + ". Hobbies include: " + Arrays.toString(developerHobbies)
                    + ". Prefered programming language in AEM is: " + developerLanguage;
        }
        return "";
    }

    @Activate
    protected void activate(Map<String, Object> properties) {
        configure(properties, "Activiated");
    }

    @Modified
    protected void modified(Map<String, Object> properties) {
        configure(properties, "Modified");
    }

    @Deactivate
    protected void deactivated(Map<String, Object> properties) {
        log.info("#############Component DeveloperInfoImpl (Deactivated) Good-bye");
    }

    protected void configure(Map<String, Object> properties, String status) {
        this.showDeveloperInfo = PropertiesUtil.toBoolean(properties.get(PROPERTY_SHOW_INFO), false);
        this.developerName = PropertiesUtil.toString(properties.get(PROPERTY_DEVELOPER_NAME), "John Doe");
        this.developerHobbies = PropertiesUtil.toStringArray(properties.get(PROPERTY_HOBBIES), new String[]{"Swimming, Walking"});
        this.developerLanguage = PropertiesUtil.toString(properties.get(PROPERTY_PROGRAMMING_LANGUAGE), "Java");
        log.info("#############Component DeveloperInfoImpl (" + status + ") " + getDeveloperInfo());
    }
}
