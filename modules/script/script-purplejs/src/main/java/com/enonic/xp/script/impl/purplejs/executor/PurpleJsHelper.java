package com.enonic.xp.script.impl.purplejs.executor;

import io.purplejs.core.resource.ResourceLoader;
import io.purplejs.core.resource.ResourcePath;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.impl.purplejs.adapter.ResourceLoaderAdapter;
import com.enonic.xp.script.impl.purplejs.adapter.ScriptExportsAdapter;

final class PurpleJsHelper
{
    ResourceLoader newResourceLoader( final ApplicationKey applicationKey, final ResourceService service )
    {
        return new ResourceLoaderAdapter( applicationKey, service );
    }

    ResourcePath toResourcePath( final ResourceKey key )
    {
        return ResourcePath.from( key.getPath() );
    }

    ScriptExports toScriptExports( final ApplicationKey applicationKey, final io.purplejs.core.value.ScriptExports from )
    {
        return new ScriptExportsAdapter( applicationKey, from );
    }
}
