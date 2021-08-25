/*
  This file created at 2020/6/21.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */
package com.rlynic.sharding.slot.database.strategy;

import com.rlynic.sharding.slot.database.configuration.ShardingAutoConfiguration;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * <code>{@link HashSlotShardingAlgorithm}</code>
 *
 * @author crisis
 */
public class HashSlotShardingAlgorithm implements PreciseShardingAlgorithm {

    private SlotDatabaseMatcher matcher;

    @Override
    public String doSharding(Collection availableTargetNames, PreciseShardingValue shardingValue) {
        if(null == matcher){
            matcher = ShardingAutoConfiguration.context.getBean(SlotDatabaseMatcher.class);
        }

        return matcher.match(shardingValue.getValue());
    }

}
