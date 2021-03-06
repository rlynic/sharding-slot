/*
  This file created at 2020/6/24.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */
package com.rlynic.sharding.slot.database.sql.token;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.rlynic.sharding.slot.database.configuration.ShardingAutoConfiguration;
import com.rlynic.sharding.slot.database.configuration.SlotShardingProperties;
import lombok.Setter;
import org.apache.shardingsphere.sql.parser.relation.statement.SQLStatementContext;
import org.apache.shardingsphere.sql.parser.relation.statement.impl.InsertSQLStatementContext;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.column.InsertColumnsSegment;
import org.apache.shardingsphere.sql.parser.sql.statement.dml.InsertStatement;
import org.apache.shardingsphere.underlying.rewrite.sql.token.generator.OptionalSQLTokenGenerator;
import org.apache.shardingsphere.underlying.rewrite.sql.token.pojo.SQLToken;
import org.apache.shardingsphere.underlying.rewrite.sql.token.pojo.generic.InsertColumnsToken;

/**
 * <code>{@link SlotSQLTokenGenerator}</code>
 *
 * @author crisis
 */
@Setter
public class SlotSQLTokenGenerator implements OptionalSQLTokenGenerator {

    private SlotShardingProperties slotShardingProperties;

    @Override
    public final boolean isGenerateSQLToken(final SQLStatementContext sqlStatementContext) {
        if(null == slotShardingProperties){
            slotShardingProperties = ShardingAutoConfiguration.context.getBean(SlotShardingProperties.class);
        }
        return sqlStatementContext instanceof InsertSQLStatementContext
                && isGenerateSQLToken((InsertStatement) sqlStatementContext.getSqlStatement());
    }

    protected boolean isGenerateSQLToken(final InsertStatement insertStatement) {
        Optional<InsertColumnsSegment> sqlSegment = insertStatement.findSQLSegment(InsertColumnsSegment.class);
        return sqlSegment.isPresent()
                && !sqlSegment.get().getColumns().isEmpty()
                && !insertStatement.getColumnNames().contains(slotShardingProperties.getColumn())
                && slotShardingProperties.getTableNames().contains(insertStatement.getTable().getTableName());
    }

    @Override
    public final SQLToken generateSQLToken(final SQLStatementContext sqlStatementContext) {
        Optional<InsertColumnsSegment> sqlSegment = sqlStatementContext.getSqlStatement().findSQLSegment(InsertColumnsSegment.class);
        Preconditions.checkState(sqlSegment.isPresent());
        return new InsertColumnsToken(sqlSegment.get().getStopIndex(), Lists.newArrayList(slotShardingProperties.getColumn()));
    }
}