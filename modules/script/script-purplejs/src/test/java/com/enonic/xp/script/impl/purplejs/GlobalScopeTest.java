package com.enonic.xp.script.impl.purplejs;

import org.junit.Test;

public class GlobalScopeTest
    extends AbstractScriptTest
{
    @Test
    public void testScope()
        throws Exception
    {
        runTestScript( "global/main.js" );
    }
}
