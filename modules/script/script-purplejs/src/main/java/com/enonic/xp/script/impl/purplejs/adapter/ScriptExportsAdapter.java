package com.enonic.xp.script.impl.purplejs.adapter;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;

public final class ScriptExportsAdapter
    implements ScriptExports
{
    private final ApplicationKey applicationKey;

    private final io.purplejs.core.value.ScriptExports exports;

    public ScriptExportsAdapter( final ApplicationKey applicationKey, final io.purplejs.core.value.ScriptExports exports )
    {
        this.applicationKey = applicationKey;
        this.exports = exports;
    }

    @Override
    public ResourceKey getScript()
    {
        return ResourceKey.from( this.applicationKey, this.exports.getResource().getPath() );
    }

    @Override
    public ScriptValue getValue()
    {
        return toValue( this.exports.getValue() );
    }

    @Override
    public boolean hasMethod( final String name )
    {
        return this.exports.hasMethod( name );
    }

    @Override
    public ScriptValue executeMethod( final String name, final Object... args )
    {
        return toValue( this.exports.executeMethod( name, args ) );
    }

    @Override
    public Object getRawValue()
    {
        return null;
    }

    private ScriptValue toValue( final io.purplejs.core.value.ScriptValue from )
    {
        return null;
    }
}
