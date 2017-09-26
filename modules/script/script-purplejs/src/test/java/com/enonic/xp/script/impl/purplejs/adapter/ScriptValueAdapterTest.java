package com.enonic.xp.script.impl.purplejs.adapter;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Sets;

import io.purplejs.core.value.ScriptValue;

import com.enonic.xp.app.ApplicationKey;

import static org.junit.Assert.*;

public class ScriptValueAdapterTest
{
    private ScriptValue mock;

    private ScriptValueAdapter adapter;

    @Before
    public void setup()
    {
        this.mock = Mockito.mock( ScriptValue.class );
        final ApplicationKey applicationKey = ApplicationKey.from( "app" );
        this.adapter = new ScriptValueAdapter( applicationKey, this.mock );
    }

    @Test
    public void isArray()
    {
        assertEquals( false, this.adapter.isArray() );

        Mockito.when( this.mock.isArray() ).thenReturn( true );
        assertEquals( true, this.adapter.isArray() );
    }

    @Test
    public void isFunction()
    {
        assertEquals( false, this.adapter.isFunction() );

        Mockito.when( this.mock.isFunction() ).thenReturn( true );
        assertEquals( true, this.adapter.isFunction() );
    }

    @Test
    public void isObject()
    {
        assertEquals( false, this.adapter.isObject() );

        Mockito.when( this.mock.isObject() ).thenReturn( true );
        assertEquals( true, this.adapter.isObject() );
    }

    @Test
    public void isValue()
    {
        assertEquals( false, this.adapter.isValue() );

        Mockito.when( this.mock.isValue() ).thenReturn( true );
        assertEquals( true, this.adapter.isValue() );
    }

    @Test
    public void getValue()
    {
        final Object expected = new Object();
        Mockito.when( this.mock.getValue() ).thenReturn( expected );

        assertSame( expected, this.adapter.getValue() );
    }

    @Test
    public void getValue_string()
    {
        Mockito.when( this.mock.getValue() ).thenReturn( null );
        assertEquals( null, this.adapter.getValue( String.class ) );

        Mockito.when( this.mock.getValue() ).thenReturn( "test" );
        assertEquals( "test", this.adapter.getValue( String.class ) );
    }

    @Test
    public void getKeys()
    {
        final Set<String> expected = Sets.newHashSet( "a", "b" );
        Mockito.when( this.mock.getKeys() ).thenReturn( expected );

        final Set<String> keys = this.adapter.getKeys();
        assertEquals( expected, keys );
    }

    @Test
    public void hasMember()
    {
        assertEquals( false, this.adapter.hasMember( "func" ) );

        Mockito.when( this.mock.hasMember( "func" ) ).thenReturn( true );
        assertEquals( true, this.adapter.hasMember( "func" ) );
    }

    @Test
    public void getMember()
    {
        final ScriptValue value = Mockito.mock( ScriptValue.class );
        Mockito.when( this.mock.getMember( "func" ) ).thenReturn( value );

        final com.enonic.xp.script.ScriptValue result = this.adapter.getMember( "func" );
        assertNotNull( result );
    }

    @Test
    public void getMember_null()
    {
        final com.enonic.xp.script.ScriptValue result = this.adapter.getMember( "func" );
        assertNull( result );
    }
}
