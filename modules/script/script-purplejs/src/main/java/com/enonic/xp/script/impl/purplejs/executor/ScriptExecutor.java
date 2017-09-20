package com.enonic.xp.script.impl.purplejs.executor;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.runtime.ScriptSettings;

public interface ScriptExecutor
{
    /*
    Application getApplication();


    Object executeRequire( ResourceKey key );

    ScriptValue newScriptValue( Object value );

    ServiceRegistry getServiceRegistry();



    JavascriptHelper getJavascriptHelper();

    void registerMock( String name, Object value );

    void registerDisposer( final ResourceKey key, Runnable callback );
    */

    ScriptExports executeMain( ResourceKey key );

    // ClassLoader getClassLoader();

    // ScriptSettings getScriptSettings();

    ResourceService getResourceService();

    void runDisposers();
}
