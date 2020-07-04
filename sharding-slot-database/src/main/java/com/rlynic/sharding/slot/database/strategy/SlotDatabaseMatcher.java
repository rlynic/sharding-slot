/*
  This file created at 2020/6/24.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */
package com.rlynic.sharding.slot.database.strategy;

import com.google.common.collect.Maps;
import com.rlynic.sharding.slot.database.CRC16;
import com.rlynic.sharding.slot.database.SlotContextHolder;
import com.rlynic.sharding.slot.database.configuration.SlotShardingProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

/**
 * <code>{@link SlotDatabaseMatcher}</code>
 *
 * @author crisis
 */
public class SlotDatabaseMatcher implements InitializingBean {
    private final static String BETWEEN_EXPRESION = "#%s between %s";

    private ExpressionParser parser = new SpelExpressionParser();
    private Map<String, Expression> slotsMapping;

    private SlotShardingProperties slotShardingProperties;

    public SlotDatabaseMatcher(SlotShardingProperties slotShardingProperties){
        this.slotShardingProperties = slotShardingProperties;
    }

    public String match(int slot){
        for (Map.Entry<String, Expression> e : slotsMapping.entrySet()) {
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setVariable(slotShardingProperties.getColumn(), slot);
            if(e.getValue().getValue(context, Boolean.class)){
                return e.getKey();
            }
        }
        throw new HashSlotRouteException(String.format("Failed to match the database, slot:'%s'", slot));
    }

    public String match(Comparable key){
        try{
            String s = String.valueOf(key);
            int slot = CRC16.CRC16_CCITT(s.getBytes()) & slotShardingProperties.getNumber() - 1;
            SlotContextHolder.add(slot);
            return match(slot);
        }catch (Throwable t){
            throw new HashSlotRouteException(String.format("failed to calculate slot, value:%s", key), t);
        }
    }

    @Override
    public void afterPropertiesSet() {
        if(null == slotsMapping){
            slotsMapping = Maps.newHashMap();

            for(Map.Entry<String, String> e : slotShardingProperties.getRange().getDatasource().entrySet()) {
                slotsMapping.put(e.getKey(), parser.parseExpression(String.format(BETWEEN_EXPRESION, slotShardingProperties.getColumn(), e.getValue())));
            }
        }
    }
}