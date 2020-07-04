/*
  This file created at 2020/6/24.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */
package com.rlynic.sharding.slot.database.configuration;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * <code>{@link SlotShardingProperties}</code>
 *
 * @author crisis
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix="slot.sharding")
public class SlotShardingProperties {

    private String column="slot";
    private int number=16384;
    private Range range;
    private List<String> datasourceNames;
    private List<String> tableNames;

    @Data
    @NoArgsConstructor
    public static class Range{
        private Map<String, String> datasource;
        private Map<String, String> table;
    }

}