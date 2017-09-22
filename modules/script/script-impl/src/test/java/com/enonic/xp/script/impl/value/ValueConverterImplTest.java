package com.enonic.xp.script.impl.value;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.script.ScriptEngine;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import com.enonic.xp.script.impl.util.JavascriptHelper;
import com.enonic.xp.script.impl.util.JavascriptHelperFactory;
import com.enonic.xp.script.impl.util.NashornHelper;

import static org.junit.Assert.*;

public class ValueConverterImplTest
{
    private ValueConverterImpl converter;

    private ScriptObjectMirror functions;

    @Before
    public void setup()
        throws Exception
    {
        final ScriptEngine engine = NashornHelper.getScriptEngine( getClass().getClassLoader() );

        final InputStream stream = getClass().getResourceAsStream( getClass().getSimpleName() + ".js" );
        final ScriptObjectMirror func = (ScriptObjectMirror) engine.eval( new InputStreamReader( stream ) );
        this.functions = (ScriptObjectMirror) func.call( null );

        final JavascriptHelper helper = new JavascriptHelperFactory( engine ).create();
        this.converter = new ValueConverterImpl( helper );
    }

    private Object fromJs( final String script )
        throws Exception
    {
        return this.converter.fromJs( this.functions.callMember( script ) );
    }

    private void toJs( final String script, final Object arg )
        throws Exception
    {
        this.functions.callMember( script, this.converter.toJs( arg ) );
    }

    @Test
    public void fromJs_int()
        throws Exception
    {
        final Object value = fromJs( "fromJs_int" );
        assertEquals( Integer.class, value.getClass() );
        assertEquals( 1, value );
    }

    @Test
    public void fromJs_double()
        throws Exception
    {
        final Object value = fromJs( "fromJs_double" );
        assertEquals( Double.class, value.getClass() );
        assertEquals( 1.5, value );
    }

    @Test
    public void fromJs_string()
        throws Exception
    {
        final Object value = fromJs( "fromJs_string" );
        assertEquals( String.class, value.getClass() );
        assertEquals( "test", value );
    }

    @Test
    public void fromJs_boolean()
        throws Exception
    {
        final Object value = fromJs( "fromJs_boolean" );
        assertEquals( Boolean.class, value.getClass() );
        assertEquals( true, value );
    }

    @Test
    public void fromJs_null()
        throws Exception
    {
        final Object value = fromJs( "fromJs_null" );
        assertNull( value );
    }

    @Test
    public void fromJs_undefined()
        throws Exception
    {
        final Object value = fromJs( "fromJs_undefined" );
        assertNull( value );
    }

    @Test
    public void fromJs_array()
        throws Exception
    {
        final Object value = fromJs( "fromJs_array" );
        assertTrue( value instanceof List );
        assertEquals( "[1, 2, 3]", value.toString() );
    }

    @Test
    public void fromJs_array_complex()
        throws Exception
    {
        final Object value = fromJs( "fromJs_array_complex" );
        assertTrue( value instanceof List );
        assertEquals( "[1, [2, 3], 4, {}]", value.toString() );
        assertTrue( ( (List) value ).get( 1 ) instanceof List );
        assertTrue( ( (List) value ).get( 3 ) instanceof Map );
    }

    @Test
    public void fromJs_object()
        throws Exception
    {
        final Object value = fromJs( "fromJs_object" );
        assertTrue( value instanceof Map );
        assertEquals( "{a=1, b=2}", value.toString() );
    }

    @Test
    public void fromJs_object_complex()
        throws Exception
    {
        final Object value = fromJs( "fromJs_object_complex" );
        assertTrue( value instanceof Map );
        assertEquals( "{a=1, b={}, c=[]}", value.toString() );
        assertTrue( ( (Map) value ).get( "b" ) instanceof Map );
        assertTrue( ( (Map) value ).get( "c" ) instanceof List );
    }

    @Test
    public void fromJs_date()
        throws Exception
    {
        final Object value = fromJs( "fromJs_date" );
        assertTrue( value instanceof Date );
        assertEquals( 1234, ( (Date) value ).getTime() );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void fromJs_function()
        throws Exception
    {
        final Object value = fromJs( "fromJs_function" );
        assertTrue( value instanceof Function );

        final Object result = ( (Function) value ).apply( null );
        assertEquals( 1, result );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void fromJs_function_args()
        throws Exception
    {
        final Object value = fromJs( "fromJs_function_args" );
        assertTrue( value instanceof Function );

        final Map<String, Object> arg1 = Maps.newHashMap();
        arg1.put( "num", 10 );

        final Date arg2 = new Date( 1234 );

        final Map<String, Object> result = (Map<String, Object>) ( (Function) value ).apply( new Object[]{arg1, arg2} );
        assertEquals( 2, result.size() );
        assertEquals( 10, result.get( "num" ) );
        assertEquals( true, result.get( "date" ) );
    }

    @Test
    public void toJs_date()
        throws Exception
    {
        toJs( "toJs_date", new Date( 1234 ) );
    }

    @Test
    public void toJs_array()
        throws Exception
    {
        toJs( "toJs_array", Lists.newArrayList( 1, 2, 3, Lists.newArrayList() ) );
        toJs( "toJs_array", new Object[]{1, 2, 3, new Object[0]} );
    }

    @Test
    public void toJs_object()
        throws Exception
    {
        final Map<String, Object> map = Maps.newHashMap();
        map.put( "a", 1 );
        map.put( "b", 2 );
        map.put( "c", Maps.newHashMap() );

        toJs( "toJs_object", map );
    }

    @Test
    public void toJs_function()
        throws Exception
    {
        final Function func = arg -> {
            final Object[] args = (Object[])arg;
            assertEquals( 1, args.length );
            assertTrue( args[0] instanceof Date );
            assertEquals( 1234, ( (Date) args[0] ).getTime() );
            return args[0];
        };

        toJs( "toJs_function", func );
    }
}
