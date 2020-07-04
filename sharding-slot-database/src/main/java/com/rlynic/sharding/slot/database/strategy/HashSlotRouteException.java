/*
  This file created at 2020/6/21.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */
package com.rlynic.sharding.slot.database.strategy;

/**
 * <code>{@link HashSlotRouteException}</code>
 *
 * hash slot路由异常
 *
 * @author crisis
 */
public class HashSlotRouteException extends RuntimeException{

    public HashSlotRouteException() {
        super();
    }

    public HashSlotRouteException(String message) {
        super(message);
    }

    public HashSlotRouteException(String message, Throwable cause) {
        super(message, cause);
    }

    public HashSlotRouteException(Throwable cause) {
        super(cause);
    }

}