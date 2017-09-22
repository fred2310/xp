package com.enonic.xp.script.impl.value;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.ScriptRuntime;

import com.enonic.xp.script.impl.util.JavascriptHelper;

public final class ValueConverterImpl
    implements ValueConverter
{
    private final JavascriptHelper helper;

    public ValueConverterImpl( final JavascriptHelper helper )
    {
        this.helper = helper;
    }

    @Override
    public Object fromJs( final Object value )
    {
        if ( value == null )
        {
            return null;
        }
        else if ( value == ScriptRuntime.UNDEFINED )
        {
            return null;
        }
        else if ( value instanceof ScriptObjectMirror )
        {
            return fromJs( (ScriptObjectMirror) value );
        }
        else
        {
            return value;
        }
    }

    private Object[] fromJs( final Object... args )
    {
        final Object[] result = new Object[args.length];
        for ( int i = 0; i < args.length; i++ )
        {
            result[i] = fromJs( args[i] );
        }

        return result;
    }

    private Object fromJs( final ScriptObjectMirror value )
    {
        if ( value.isArray() )
        {
            return fromJsArray( value );
        }
        else if ( value.isFunction() )
        {
            return fromJsFunction( value );
        }

        final String className = value.getClassName();
        if ( "date".equalsIgnoreCase( className ) )
        {
            return fromJsDate( value );
        }

        return fromJsMap( value );
    }

    private Object fromJsArray( final ScriptObjectMirror value )
    {
        final List<Object> list = Lists.newArrayList();
        for ( int i = 0; i < value.size(); i++ )
        {
            list.add( fromJs( value.getSlot( i ) ) );
        }

        return list;
    }

    private Object fromJsMap( final ScriptObjectMirror value )
    {
        final Map<String, Object> map = Maps.newHashMap();
        for ( Map.Entry<String, Object> entry : value.entrySet() )
        {
            map.put( entry.getKey(), fromJs( entry.getValue() ) );
        }

        return map;
    }

    private Object fromJsDate( final ScriptObjectMirror value )
    {
        final Number time = (Number) value.callMember( "getTime" );
        return new Date( time.longValue() );
    }

    private Object fromJsFunction( final ScriptObjectMirror value )
    {
        return (Function) args -> {
            final Object result = value.call( null, toJs( toFunctionArgs( args ) ) );
            return fromJs( result );
        };
    }

    private Object[] toFunctionArgs( final Object args )
    {
        if ( args == null )
        {
            return new Object[0];
        }
        else if ( args instanceof Object[] )
        {
            return (Object[]) args;
        }
        else
        {
            return new Object[]{args};
        }
    }

    @Override
    public Object toJs( final Object value )
    {
        if ( value instanceof Date )
        {
            return toJs( (Date) value );
        }
        else if ( value instanceof Map )
        {
            return toJs( (Map) value );
        }
        else if ( value instanceof Object[] )
        {
            return toJs( Arrays.asList( (Object[]) value ) );
        }
        else if ( value instanceof Iterable )
        {
            return toJs( (Iterable) value );
        }
        else if ( value instanceof Function )
        {
            return toJs( (Function) value );
        }

        return value;
    }

    private Object toJs( final Date value )
    {
        return this.helper.newDateObject( value.getTime() );
    }

    private Object toJs( final Iterable<?> value )
    {
        final ScriptObjectMirror result = this.helper.newJsArray();
        for ( final Object entry : value )
        {
            result.callMember( "push", toJs( entry ) );
        }

        return result;
    }

    private Object toJs( final Map<?, ?> value )
    {
        final ScriptObjectMirror result = this.helper.newJsObject();
        for ( final Map.Entry<?, ?> entry : value.entrySet() )
        {
            result.put( entry.getKey().toString(), toJs( entry.getValue() ) );
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private Object toJs( final Function value )
    {
        return (Function) o -> {
            final Object[] args = fromJs( toFunctionArgs( o ) );
            return toJs( value.apply( args ) );
        };
    }

    private Object[] toJs( final Object[] value )
    {
        final Object[] result = new Object[value.length];
        for ( int i = 0; i < value.length; i++ )
        {
            result[i] = toJs( value[i] );
        }

        return result;
    }
}
