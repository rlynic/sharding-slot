/*
  This file created at 2020/6/25.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */
package com.rlynic.sharding.slot.database.sql.token;

import com.google.common.base.Preconditions;
import com.rlynic.sharding.slot.database.SlotContextHolder;
import lombok.Setter;
import org.apache.shardingsphere.sql.parser.binder.segment.insert.values.InsertValueContext;
import org.apache.shardingsphere.sql.parser.binder.segment.insert.values.expression.DerivedLiteralExpressionSegment;
import org.apache.shardingsphere.sql.parser.binder.segment.insert.values.expression.DerivedParameterMarkerExpressionSegment;
import org.apache.shardingsphere.sql.parser.binder.segment.insert.values.expression.DerivedSimpleExpressionSegment;
import org.apache.shardingsphere.sql.parser.binder.statement.dml.InsertStatementContext;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.column.InsertColumnsSegment;
import org.apache.shardingsphere.sql.parser.sql.statement.dml.InsertStatement;
import org.apache.shardingsphere.underlying.rewrite.sql.token.generator.aware.PreviousSQLTokensAware;
import org.apache.shardingsphere.underlying.rewrite.sql.token.pojo.SQLToken;
import org.apache.shardingsphere.underlying.rewrite.sql.token.pojo.generic.InsertValue;
import org.apache.shardingsphere.underlying.rewrite.sql.token.pojo.generic.InsertValuesToken;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * <code>{@link SlotInsertValueSQLToken}</code>
 *
 * @author crisis
 */
@Setter
public class SlotInsertValueSQLToken extends AbstractSQLTokenGenerator implements PreviousSQLTokensAware {
    private List<SQLToken> previousSQLTokens;

    @Override
    protected boolean isGenerateSQLToken(final InsertStatement insertStatement) {
        Optional<InsertColumnsSegment> sqlSegment = insertStatement.getInsertColumns();
        return sqlSegment.isPresent() && !sqlSegment.get().getColumns().isEmpty()
                && slotShardingProperties.getTableNames().contains(insertStatement.getTable().getTableName().getIdentifier().getValue());
    }

    @Override
    public final SQLToken  generateSQLToken(final InsertStatementContext insertStatementContext) {
        try {
            Optional<InsertValuesToken> result = findPreviousSQLToken();
            Preconditions.checkState(result.isPresent());

            Iterator<Integer> slots = SlotContextHolder.get().iterator();

            int count = 0;
            for (InsertValueContext each : insertStatementContext.getInsertValueContexts()) {
                InsertValue insertValueToken = result.get().getInsertValues().get(count);
                DerivedSimpleExpressionSegment expressionSegment = isToAddDerivedLiteralExpression(insertStatementContext, count)
                        ? new DerivedLiteralExpressionSegment(slots.next()) : new DerivedParameterMarkerExpressionSegment(each.getParametersCount());
                insertValueToken.getValues().add(expressionSegment);
                count++;
            }

            return result.get();
        }finally {
            SlotContextHolder.clear();
        }

    }

    private Optional<InsertValuesToken> findPreviousSQLToken() {
        for (SQLToken each : previousSQLTokens) {
            if (each instanceof InsertValuesToken) {
                return Optional.of((InsertValuesToken) each);
            }
        }
        return Optional.empty();
    }

    private boolean isToAddDerivedLiteralExpression(final InsertStatementContext insertStatementContext, final int insertValueCount) {
        return insertStatementContext.getGroupedParameters().get(insertValueCount).isEmpty();
    }
}
