/*
  This file created at 2020/6/25.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */
package com.rlynic.sharding.slot.database;

import com.google.common.collect.Lists;

import java.util.LinkedList;

/**
 * <code>{@link SlotContextHolder}</code>
 *
 * @author crisis
 */
public class SlotContextHolder {

    private static ThreadLocal<LinkedList<Integer>> slotsContext = new ThreadLocal<>();

    public static void add(int slot){
        LinkedList<Integer> slots = slotsContext.get();
        if(null == slots){
            slots = Lists.newLinkedList();
            slotsContext.set(slots);
        }
        slots.add(slot);
    }

    public static LinkedList<Integer> get(){
        return slotsContext.get();
    }

    public static void clear(){
        slotsContext.remove();
    }

}