/*
  This file created at 2021/8/25.

  Copyright (c) 2002-2021 crisis, Inc. All rights reserved.
 */
package com.rlynic.sharding.slot.database.sql.token;

import com.rlynic.sharding.slot.database.SlotContextHolder;
import com.rlynic.sharding.slot.database.configuration.ShardingAutoConfiguration;
import com.rlynic.sharding.slot.database.configuration.SlotShardingProperties;
import org.apache.shardingsphere.sql.parser.relation.statement.SQLStatementContext;
import org.apache.shardingsphere.sql.parser.relation.statement.impl.InsertSQLStatementContext;
import org.apache.shardingsphere.sql.parser.sql.statement.dml.InsertStatement;
import org.apache.shardingsphere.underlying.rewrite.sql.token.generator.OptionalSQLTokenGenerator;

/**
 * <code>{@link AbstractSQLTokenGenerator}</code>
 *
 * @author crisis
 */
public abstract class AbstractSQLTokenGenerator implements OptionalSQLTokenGenerator {

    protected SlotShardingProperties slotShardingProperties;

    @Override
    public boolean isGenerateSQLToken(final SQLStatementContext sqlStatementContext) {
        if(null == slotShardingProperties){
            slotShardingProperties = ShardingAutoConfiguration.context.getBean(SlotShardingProperties.class);
        }
        boolean isSlot = sqlStatementContext instanceof InsertSQLStatementContext
                && isGenerateSQLToken((InsertStatement) sqlStatementContext.getSqlStatement());
        if(!isSlot){
            SlotContextHolder.clear();
        }
        return isSlot;
    }

    protected abstract boolean isGenerateSQLToken(final InsertStatement insertStatement);
}
