/*
  This file created at 2020/6/21.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */
package com.rlynic.sharding.slot.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.List;

/**
 * <code>{@link ShardingEnvironmentPostProcessor}</code>
 *
 * @author crisis
 */
public class ShardingEnvironmentPostProcessor implements EnvironmentPostProcessor {
    private final static Logger log = LoggerFactory.getLogger(ShardingEnvironmentPostProcessor.class);

    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    private PropertiesPropertySourceLoader propertySourcesLoader = new PropertiesPropertySourceLoader();

    private String[] propertiesLocations = {
            "classpath:/META-INF/sharding-constants.properties"
    };

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try{
            for(String l : propertiesLocations){
                Resource r = resourceLoader.getResource(l);
                if(null != r && r.exists()){
                    List<PropertySource<?>> propertySource = propertySourcesLoader.load("sharding config: [profile=default]", r);
                    propertySource.forEach(p -> environment.getPropertySources().addLast(p));
                }
            }
        }catch(IOException e){
            log.error("the sharding config failed to load", e);
        }
    }
}
