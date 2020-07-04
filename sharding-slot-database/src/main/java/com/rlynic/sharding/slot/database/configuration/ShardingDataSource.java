/*
  This file created at 2020/6/24.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */
package com.rlynic.sharding.slot.database.configuration;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.apache.shardingsphere.shardingjdbc.jdbc.adapter.AbstractDataSourceAdapter;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.connection.ShardingConnection;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.context.ShardingRuntimeContext;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.MasterSlaveDataSource;
import org.apache.shardingsphere.transaction.core.TransactionTypeHolder;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * <code>{@link ShardingDataSource}</code>
 *
 * @author crisis
 */
@Getter
public class ShardingDataSource extends AbstractDataSourceAdapter {

    private final ShardingRuntimeContext runtimeContext;

    public ShardingDataSource(final Map<String, DataSource> dataSourceMap, final ShardingRule shardingRule, final Properties props) throws SQLException {
        super(dataSourceMap);
        checkDataSourceType(dataSourceMap);
        runtimeContext = new ShardingRuntimeContext(dataSourceMap, shardingRule, props, getDatabaseType());
    }

    private void checkDataSourceType(final Map<String, DataSource> dataSourceMap) {
        for (DataSource each : dataSourceMap.values()) {
            Preconditions.checkArgument(!(each instanceof MasterSlaveDataSource), "Initialized data sources can not be master-slave data sources.");
        }
    }

    @Override
    public final ShardingConnection getConnection() {
        return new ShardingConnection(getDataSourceMap(), runtimeContext, TransactionTypeHolder.get());
    }
}