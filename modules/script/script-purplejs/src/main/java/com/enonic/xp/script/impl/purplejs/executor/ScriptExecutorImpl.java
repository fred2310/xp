package com.enonic.xp.script.impl.purplejs.executor;

import com.enonic.xp.app.Application;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.server.RunMode;

final class ScriptExecutorImpl
    implements ScriptExecutor
{
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
        return null;
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
    }
}

