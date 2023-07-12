package com.blueofflaner;

import static org.junit.Assert.assertTrue;

import com.blueofflaner.common.message.ResponseStatus;
import com.blueofflaner.common.message.RpcResponseMessage;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void test() {
        RpcResponseMessage responseMessage = new RpcResponseMessage();
        responseMessage.setSequenceId("1");
        responseMessage.fail(ResponseStatus.NO_SUCH_METHOD);
        System.out.println(responseMessage);
    }
}
