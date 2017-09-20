package com.enonic.xp.script.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.script.runtime.ScriptRuntime;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.script.runtime.ScriptSettings;

@Component(immediate = true, property = "provider=delegate")
public final class ScriptRuntimeFactoryDelegate
    implements ScriptRuntimeFactory
{
    private final static String SWITCH_PROP = "xp.usePurpleJs";

    private ScriptRuntimeFactory standardProvider;

    private ScriptRuntimeFactory purpleJsProvider;

    private ScriptRuntimeFactory provider;

    @Activate
    public void activate()
    {
        final boolean usePurpleJs = "true".equalsIgnoreCase( System.getProperty( SWITCH_PROP, "false" ) );
        this.provider = usePurpleJs ? this.purpleJsProvider : this.standardProvider;
    }

    @Override
    public ScriptRuntime create( final ScriptSettings settings )
    {
        return this.provider.create( settings );
    }

    @Override
    public void dispose( final ScriptRuntime runtime )
    {
        this.provider.dispose( runtime );
    }

    @Reference(target = "(provider=standard)")
    public void setStandardProvider( final ScriptRuntimeFactory provider )
    {
        this.standardProvider = provider;
    }

    @Reference(target = "(provider=purpleJs)")
    public void setPurpleJsProvider( final ScriptRuntimeFactory provider )
    {
        this.purpleJsProvider = provider;
    }
}
