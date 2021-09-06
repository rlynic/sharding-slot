/*
  This file created at 2020/6/24.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */
package com.rlynic.sharding.slot.database.sql.token;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.Setter;
import org.apache.shardingsphere.sql.parser.binder.statement.dml.InsertStatementContext;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.column.InsertColumnsSegment;
import org.apache.shardingsphere.sql.parser.sql.statement.dml.InsertStatement;
import org.apache.shardingsphere.underlying.rewrite.sql.token.pojo.SQLToken;
import org.apache.shardingsphere.underlying.rewrite.sql.token.pojo.generic.InsertColumnsToken;

import java.util.Optional;

/**
 * <code>{@link SlotSQLTokenGenerator}</code>
 *
 * @author crisis
 */
@Setter
public class SlotSQLTokenGenerator extends AbstractSQLTokenGenerator {

    @Override
    protected boolean isGenerateSQLToken(final InsertStatement insertStatement) {
        Optional<InsertColumnsSegment> sqlSegment = insertStatement.getInsertColumns();
        return sqlSegment.isPresent()
                && !sqlSegment.get().getColumns().isEmpty()
                && !insertStatement.getColumnNames().contains(slotShardingProperties.getColumn())
                && slotShardingProperties.getTableNames().contains(insertStatement.getTable().getTableName().getIdentifier().getValue());
    }

    @Override
    public final SQLToken generateSQLToken(final InsertStatementContext insertStatementContext) {
        Optional<InsertColumnsSegment> sqlSegment = insertStatementContext.getSqlStatement().getInsertColumns();
        Preconditions.checkState(sqlSegment.isPresent());
        return new InsertColumnsToken(sqlSegment.get().getStopIndex(), Lists.newArrayList(slotShardingProperties.getColumn()));
    }
}
