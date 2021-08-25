/*
  This file created at 2020/6/25.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */
package com.rlynic.sharding.slot.database.sql.token;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.rlynic.sharding.slot.database.SlotContextHolder;
import com.rlynic.sharding.slot.database.configuration.SlotShardingProperties;
import lombok.Setter;
import org.apache.shardingsphere.sql.parser.relation.segment.insert.InsertValueContext;
import org.apache.shardingsphere.sql.parser.relation.segment.insert.expression.DerivedLiteralExpressionSegment;
import org.apache.shardingsphere.sql.parser.relation.segment.insert.expression.DerivedParameterMarkerExpressionSegment;
import org.apache.shardingsphere.sql.parser.relation.segment.insert.expression.DerivedSimpleExpressionSegment;
import org.apache.shardingsphere.sql.parser.relation.statement.SQLStatementContext;
import org.apache.shardingsphere.sql.parser.relation.statement.impl.InsertSQLStatementContext;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.assignment.InsertValuesSegment;
import org.apache.shardingsphere.sql.parser.sql.statement.dml.InsertStatement;
import org.apache.shardingsphere.underlying.rewrite.sql.token.generator.aware.PreviousSQLTokensAware;
import org.apache.shardingsphere.underlying.rewrite.sql.token.pojo.SQLToken;
import org.apache.shardingsphere.underlying.rewrite.sql.token.pojo.generic.InsertValue;
import org.apache.shardingsphere.underlying.rewrite.sql.token.pojo.generic.InsertValuesToken;

import java.util.Iterator;
import java.util.List;

/**
 * <code>{@link SlotInsertValueSQLToken}</code>
 *
 * @author crisis
 */
@Setter
public class SlotInsertValueSQLToken extends AbstractSQLTokenGenerator implements PreviousSQLTokensAware {
    private List<SQLToken> previousSQLTokens;

    private SlotShardingProperties slotShardingProperties;

    @Override
    protected boolean isGenerateSQLToken(final InsertStatement insertStatement) {
        return !insertStatement.findSQLSegments(InsertValuesSegment.class).isEmpty()
                && slotShardingProperties.getTableNames().contains(insertStatement.getTable().getTableName());
    }

    @Override
    public final SQLToken  generateSQLToken(final SQLStatementContext sqlStatementContext) {
        try {
            Optional<InsertValuesToken> result = findPreviousSQLToken();
            Preconditions.checkState(result.isPresent());

            Iterator<Integer> slots = SlotContextHolder.get().iterator();
            int count = 0;
            for (InsertValueContext each : ((InsertSQLStatementContext) sqlStatementContext).getInsertValueContexts()) {
                InsertValue insertValueToken = result.get().getInsertValues().get(count);
                DerivedSimpleExpressionSegment expressionSegment = isToAddDerivedLiteralExpression((InsertSQLStatementContext) sqlStatementContext, count)
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
        return Optional.absent();
    }

    private boolean isToAddDerivedLiteralExpression(final InsertSQLStatementContext insertSQLStatementContext, final int insertValueCount) {
        return insertSQLStatementContext.getGroupedParameters().get(insertValueCount).isEmpty();
    }
}
