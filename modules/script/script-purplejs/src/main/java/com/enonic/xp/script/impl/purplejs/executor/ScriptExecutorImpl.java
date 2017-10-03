package com.enonic.xp.script.impl.purplejs.executor;

import java.util.Map;

import io.purplejs.core.Engine;
import io.purplejs.core.EngineBinder;
import io.purplejs.core.EngineBuilder;
import io.purplejs.core.EngineModule;
import io.purplejs.core.require.RequireResolverBuilder;

import com.enonic.xp.app.Application;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.impl.purplejs.function.ApplicationInfoBuilder;
import com.enonic.xp.script.impl.purplejs.function.ScriptFunctions;
import com.enonic.xp.script.impl.purplejs.service.ServiceRegistry;
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

    private ServiceRegistry serviceRegistry;

    private Application application;

    private RunMode runMode;

    @Override
    public ResourceService getResourceService()
    {
        return this.resourceService;
    }

    @Override
    public Application getApplication()
    {
        return this.application;
    }

    public ServiceRegistry getServiceRegistry()
    {
        return this.serviceRegistry;
    }

    @Override
    public ScriptSettings getScriptSettings()
    {
        return this.scriptSettings;
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
    public void dispose()
    {
        this.engine.dispose();
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

    public void setServiceRegistry( final ServiceRegistry serviceRegistry )
    {
        this.serviceRegistry = serviceRegistry;
    }

    void initialize()
    {
        this.engine = EngineBuilder.newBuilder().
            classLoader( this.classLoader ).
            resourceLoader( this.helper.newResourceLoader( this.application.getKey(), this.resourceService ) ).
            requireResolver( RequireResolverBuilder.newBuilder().
                rootPath( "/", "/site" ).
                searchPath( "/lib", "/site/lib" ).
                build() ).
            module( this ).
            build();
    }

    @Override
    public void configure( final EngineBinder binder )
    {
        final ScriptFunctions functions = new ScriptFunctions( this );
        binder.globalVariable( "__", functions );
        binder.globalVariable( "app", buildAppInfo() );
    }

    private Map<String, Object> buildAppInfo()
    {
        final ApplicationInfoBuilder builder = new ApplicationInfoBuilder();
        builder.application( this.application );
        return builder.build();
    }
}
