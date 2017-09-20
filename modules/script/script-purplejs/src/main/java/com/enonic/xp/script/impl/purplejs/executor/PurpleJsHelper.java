package com.enonic.xp.script.impl.purplejs.executor;

import io.purplejs.core.exception.NotFoundException;
import io.purplejs.core.exception.ProblemException;
import io.purplejs.core.json.JsonSerializable;
import io.purplejs.core.resource.ResourceLoader;
import io.purplejs.core.resource.ResourcePath;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.impl.purplejs.adapter.MapGeneratorAdapter;
import com.enonic.xp.script.impl.purplejs.adapter.ResourceLoaderAdapter;
import com.enonic.xp.script.impl.purplejs.adapter.ScriptExportsAdapter;
import com.enonic.xp.script.serializer.MapSerializable;

public final class PurpleJsHelper
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

    public static RuntimeException translateException( final ApplicationKey applicationKey, final RuntimeException e )
    {
        if ( e instanceof NotFoundException )
        {
            return translateException( applicationKey, (NotFoundException) e );
        }
        else if ( e instanceof ProblemException )
        {
            return translateException( applicationKey, (ProblemException) e );
        }
        else
        {
            return e;
        }
    }

    private static RuntimeException translateException( final ApplicationKey applicationKey, final NotFoundException e )
    {
        // TODO: Translate when we can get path from exception.
        return e;
    }

    private static ResourceProblemException translateException( final ApplicationKey applicationKey, final ProblemException e )
    {
        return ResourceProblemException.create().
            cause( e.getCause() ).
            resource( ResourceKey.from( applicationKey, e.getPath().getPath() ) ).
            lineNumber( e.getLineNumber() ).
            build();
    }

    public static Object toJsObject( final Object value )
    {
        if ( value instanceof MapSerializable )
        {
            return toJsObject( (MapSerializable) value );
        }
        else
        {
            return value;
        }
    }

    private static Object toJsObject( final MapSerializable value )
    {
        return (JsonSerializable) gen -> {
            gen.map();
            value.serialize( new MapGeneratorAdapter( gen ) );
            gen.end();
        };
    }

    public static Object[] toJsObjects( final Object... args )
    {
        final Object[] newArgs = new Object[args.length];
        for ( int i = 0; i < args.length; i++ )
        {
            newArgs[i] = toJsObject( args[i] );
        }

        return newArgs;
    }
}
