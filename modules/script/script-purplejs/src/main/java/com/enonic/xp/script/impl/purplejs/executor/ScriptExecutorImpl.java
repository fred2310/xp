package com.enonic.xp.script.impl.purplejs.executor;

import io.purplejs.core.Engine;
import io.purplejs.core.EngineBinder;
import io.purplejs.core.EngineBuilder;
import io.purplejs.core.EngineModule;

import com.enonic.xp.app.Application;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.impl.purplejs.function.ApplicationVariable;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.server.RunMode;

final class ScriptExecutorImpl
    implements ScriptExecutor, EngineModule
{
    private final PurpleJsHelper helper = new PurpleJsHelper();

    private Engine engine;

    private ScriptSettings scriptSettings;

    private ClassLoader classLoader;

    private ResourceService resourceService;

    private Application application;

    private RunMode runMode;

    @Override
    public ResourceService getResourceService()
    {
        return resourceService;
    }

    @Override
    public ScriptExports executeMain( final ResourceKey key )
    {
        try
        {
            return this.helper.toScriptExports( this.application.getKey(), this.engine.require( this.helper.toResourcePath( key ) ) );
        }
        catch ( final RuntimeException e )
        {
            throw PurpleJsHelper.translateException( this.application.getKey(), e );
        }
    }

    @Override
    public void runDisposers()
    {
        // this.disposers.values().forEach( Runnable::run );
    }

    void setScriptSettings( final ScriptSettings scriptSettings )
    {
        this.scriptSettings = scriptSettings;
    }

    void setClassLoader( final ClassLoader classLoader )
    {
        this.classLoader = classLoader;
    }

    void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    void setApplication( final Application application )
    {
        this.application = application;
    }

    void setRunMode( final RunMode runMode )
    {
        this.runMode = runMode;
    }

    void initialize()
    {
        this.engine = EngineBuilder.newBuilder().
            classLoader( this.classLoader ).
            resourceLoader( this.helper.newResourceLoader( this.application.getKey(), this.resourceService ) ).
            module( this ).
            build();
    }

    @Override
    public void configure( final EngineBinder binder )
    {
        binder.globalVariable( "app", new ApplicationVariable( this.application ) );
    }
}
