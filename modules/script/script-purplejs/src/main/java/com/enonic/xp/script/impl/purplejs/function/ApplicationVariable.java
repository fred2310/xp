package com.enonic.xp.script.impl.purplejs.function;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.xp.app.Application;

public final class ApplicationVariable
{
    private final Application application;

    public ApplicationVariable( final Application application )
    {
        this.application = application;
    }

    public String getName()
    {
        return this.application.getKey().toString();
    }

    public String getVersion()
    {
        return this.application.getVersion().toString();
    }

    // TODO: config does not work with JSON.stringify since it's not a real javascript object,
    public Map<String, String> getConfig()
    {
        return Maps.newHashMap();
    }
}
