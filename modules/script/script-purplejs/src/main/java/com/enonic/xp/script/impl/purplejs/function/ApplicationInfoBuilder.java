package com.enonic.xp.script.impl.purplejs.function;

import java.util.HashMap;
import java.util.Map;

import com.enonic.xp.app.Application;
import com.enonic.xp.config.Configuration;

public final class ApplicationInfoBuilder
{
    private Application application;


    public ApplicationInfoBuilder application( final Application application )
    {
        this.application = application;
        return this;
    }

    public Map<String, Object> build()
    {
        final Map<String, Object> result = new HashMap<>();
        result.put( "name", toString( this.application.getKey() ) );
        result.put( "version", toString( this.application.getVersion() ) );
        result.put( "config", buildConfig() );
        return result;
    }

    private Map<String, Object> buildConfig()
    {
        final Map<String, Object> result = new HashMap<>();
        final Configuration config = this.application.getConfig();

        if ( config != null )
        {
            result.putAll( config.asMap() );
        }

        return result;
    }

    private String toString( final Object value )
    {
        return value != null ? value.toString() : "";
    }
}
