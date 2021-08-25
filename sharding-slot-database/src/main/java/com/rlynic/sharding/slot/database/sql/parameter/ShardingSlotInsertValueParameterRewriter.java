/*
  This file created at 2020/6/25.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */
package com.rlynic.sharding.slot.database.sql.parameter;

import com.google.common.collect.Lists;
import com.rlynic.sharding.slot.database.SlotContextHolder;
import com.rlynic.sharding.slot.database.configuration.ShardingAutoConfiguration;
import com.rlynic.sharding.slot.database.configuration.SlotShardingProperties;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shardingsphere.sql.parser.relation.statement.SQLStatementContext;
import org.apache.shardingsphere.sql.parser.relation.statement.impl.InsertSQLStatementContext;
import org.apache.shardingsphere.sql.parser.sql.statement.dml.InsertStatement;
import org.apache.shardingsphere.underlying.rewrite.parameter.builder.ParameterBuilder;
import org.apache.shardingsphere.underlying.rewrite.parameter.builder.impl.GroupedParameterBuilder;
import org.apache.shardingsphere.underlying.rewrite.parameter.rewriter.ParameterRewriter;

import java.util.Iterator;
import java.util.List;

/**
 * <code>{@link ShardingSlotInsertValueParameterRewriter}</code>
 *
 * @author crisis
 */
public class ShardingSlotInsertValueParameterRewriter implements ParameterRewriter {
    private SlotShardingProperties slotShardingProperties;

    @Override
    public boolean isNeedRewrite(final SQLStatementContext sqlStatementContext) {
        if(null == slotShardingProperties){
            slotShardingProperties = ShardingAutoConfiguration.context.getBean(SlotShardingProperties.class);
        }
        return sqlStatementContext instanceof InsertSQLStatementContext
                && slotShardingProperties.getTableNames().contains(((InsertStatement) sqlStatementContext.getSqlStatement()).getTable().getTableName());
    }

    @Override
    public void rewrite(final ParameterBuilder parameterBuilder, final SQLStatementContext sqlStatementContext, final List<Object> parameters) {
        List<Integer> slotsContext = SlotContextHolder.get();
        if(CollectionUtils.isEmpty(slotsContext)){
            return;
        }
        Iterator<Integer> slots = slotsContext.iterator();
        int count = 0;

        List<String> columnNames = ((InsertStatement) sqlStatementContext.getSqlStatement()).getColumnNames();
        int cIndex = columnNames.indexOf(slotShardingProperties.getColumn()) + 1;
        for (List<Object> each : ((InsertSQLStatementContext) sqlStatementContext).getGroupedParameters()) {
            if(cIndex <= 0){
                cIndex = ((GroupedParameterBuilder) parameterBuilder).getParameterBuilders().get(count).getParameters().size();
            }

            Comparable<?> generatedValue = slots.next();
            if (!each.isEmpty()) {
                ((GroupedParameterBuilder) parameterBuilder).getParameterBuilders().get(count)
                        .addAddedParameters(cIndex, Lists.newArrayList(generatedValue));
            }
            count++;
        }
    }
}
