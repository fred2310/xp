package com.enonic.xp.script.impl.purplejs.adapter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import io.purplejs.core.resource.ResourcePath;
import io.purplejs.core.value.ScriptExports;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptValue;

import static org.junit.Assert.*;

public class ScriptExportsAdapterTest
{
    private ScriptExports mock;

    private ScriptExportsAdapter adapter;

    @Before
    public void setup()
    {
        this.mock = Mockito.mock( ScriptExports.class );
        final ApplicationKey applicationKey = ApplicationKey.from( "app" );
        this.adapter = new ScriptExportsAdapter( applicationKey, this.mock );
    }

    @Test
    public void getScript()
    {
        Mockito.when( this.mock.getResource() ).thenReturn( ResourcePath.from( "/a/b.js" ) );

        final ResourceKey key = this.adapter.getScript();
        assertNotNull( key );
        assertEquals( "app:/a/b.js", key.toString() );
    }

    private io.purplejs.core.value.ScriptValue mockValue()
    {
        return Mockito.mock( io.purplejs.core.value.ScriptValue.class );
    }

    @Test
    public void getValue()
    {
        final io.purplejs.core.value.ScriptValue mockValue = mockValue();
        Mockito.when( this.mock.getValue() ).thenReturn( mockValue );

        final ScriptValue value = this.adapter.getValue();
        assertNotNull( value );
    }

    @Test
    public void getValue_null()
    {
        final ScriptValue value = this.adapter.getValue();
        assertNull( value );
    }

    @Test
    public void hasMethod()
    {
        Mockito.when( this.mock.hasMethod( "exists" ) ).thenReturn( true );
        assertEquals( true, this.adapter.hasMethod( "exists" ) );
        assertEquals( false, this.adapter.hasMethod( "notFound" ) );
    }

    @Test
    public void executeMethod()
    {
        final io.purplejs.core.value.ScriptValue mockValue = mockValue();
        Mockito.when( this.mock.executeMethod( "func" ) ).thenReturn( mockValue );

        final ScriptValue result = this.adapter.executeMethod( "func" );
        assertNotNull( result );
    }

    @Test
    public void executeMethod_notFound()
    {
        final ScriptValue result = this.adapter.executeMethod( "func" );
        assertNull( result );
    }

    @Test(expected = RuntimeException.class)
    public void executeMethod_exception()
    {
        Mockito.when( this.mock.executeMethod( "func" ) ).thenThrow( new RuntimeException() );
        this.adapter.executeMethod( "func" );
    }

    @Test
    public void getRawValue()
    {
        final Object rawValue = new Object();

        io.purplejs.core.value.ScriptValue mockValue = mockValue();
        Mockito.when( mockValue.getRaw() ).thenReturn( rawValue );

        Mockito.when( this.mock.getValue() ).thenReturn( mockValue );

        final Object result = this.adapter.getRawValue();
        assertNotNull( result );
        assertSame( rawValue, result );
    }
}
