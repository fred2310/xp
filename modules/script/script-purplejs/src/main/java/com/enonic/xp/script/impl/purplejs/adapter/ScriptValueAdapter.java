package com.enonic.xp.script.impl.purplejs.adapter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.convert.Converters;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.purplejs.executor.PurpleJsHelper;

final class ScriptValueAdapter
    implements ScriptValue
{
    private final ApplicationKey applicationKey;

    private final io.purplejs.core.value.ScriptValue value;

    ScriptValueAdapter( final ApplicationKey applicationKey, final io.purplejs.core.value.ScriptValue value )
    {
        this.applicationKey = applicationKey;
        this.value = value;
    }

    @Override
    public boolean isArray()
    {
        return this.value.isArray();
    }

    @Override
    public boolean isObject()
    {
        return this.value.isObject();
    }

    @Override
    public boolean isValue()
    {
        return this.value.isValue();
    }

    @Override
    public boolean isFunction()
    {
        return this.value.isFunction();
    }

    @Override
    public Object getValue()
    {
        return this.value.getValue();
    }

    @Override
    public <T> T getValue( final Class<T> type )
    {
        final Object value = this.value.getValue();
        return value != null ? Converters.convert( value, type ) : null;
    }

    @Override
    public Set<String> getKeys()
    {
        return this.value.getKeys();
    }

    @Override
    public boolean hasMember( final String key )
    {
        return this.value.hasMember( key );
    }

    @Override
    public ScriptValue getMember( final String key )
    {
        final io.purplejs.core.value.ScriptValue value = this.value.getMember( key );
        return value != null ? new ScriptValueAdapter( this.applicationKey, value ) : null;
    }

    @Override
    public List<ScriptValue> getArray()
    {
        return this.value.getArray().stream().map( e -> new ScriptValueAdapter( this.applicationKey, e ) ).collect( Collectors.toList() );
    }

    @Override
    public <T> List<T> getArray( final Class<T> type )
    {
        throw new IllegalStateException( "Not implemented" );
    }

    @Override
    public Map<String, Object> getMap()
    {
        throw new IllegalStateException( "Not implemented" );
    }

    @Override
    public ScriptValue call( final Object... args )
    {
        try
        {
            final io.purplejs.core.value.ScriptValue value = this.value.call( PurpleJsHelper.toJsObjects( args ) );
            return value != null ? new ScriptValueAdapter( this.applicationKey, value ) : null;
        }
        catch ( final RuntimeException e )
        {
            throw PurpleJsHelper.translateException( this.applicationKey, e );
        }
    }
}
