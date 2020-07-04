/*
  This file created at 2020/6/21.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */

import com.rlynic.sharding.slot.database.CRC16;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

/**
 * <code>{@link HashSlotCalcCase}</code>
 *
 * @author crisis
 */
public class HashSlotCalcCase {

    private String id;

    @Before
    public void before(){
        this.id = UUID.randomUUID().toString();
    }

    @Test
    public void testCRC16UUID(){
        Assert.assertNotNull(CRC16.CRC16_CCITT(id.getBytes()));
    }

    @Test
    public void testUUIDHashSlot(){
        int c = CRC16.CRC16_CCITT(id.getBytes());
        Assert.assertTrue((c & 16383) < 16384);
    }

}