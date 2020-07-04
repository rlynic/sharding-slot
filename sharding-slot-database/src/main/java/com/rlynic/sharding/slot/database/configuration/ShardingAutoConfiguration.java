/*
  This file created at 2020/6/24.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */
package com.rlynic.sharding.slot.database.configuration;

import com.rlynic.sharding.slot.database.strategy.SlotDatabaseMatcher;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <code>{@link ShardingAutoConfiguration}</code>
 *
 * @author crisis
 */
@Configuration
@EnableConfigurationProperties({
    SlotShardingProperties.class
})
public class ShardingAutoConfiguration implements ApplicationContextAware {

    public static ApplicationContext context;

    @Bean
    public SlotDatabaseMatcher slotDatabaseMatcher(SlotShardingProperties slotShardingProperties){
        return new SlotDatabaseMatcher(slotShardingProperties);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}