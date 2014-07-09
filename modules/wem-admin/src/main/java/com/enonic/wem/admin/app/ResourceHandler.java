package com.enonic.wem.admin.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.io.ByteStreams;
import com.google.common.net.MediaType;

import com.enonic.wem.api.util.MediaTypes;

final class ResourceHandler
{

    private static final String INDEX_HTML = "index.html";

    private final ServletContext servletContext;

    private final ResourceLocator resourceLocator;

    public ResourceHandler( final ServletContext servletContext, final ResourceLocator resourceLocator )
    {
        this.servletContext = servletContext;
        this.resourceLocator = resourceLocator;
    }

    public void handle( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        final String path = req.getRequestURI().substring( req.getContextPath().length() );
        final InputStream in = findResource( path );

        if ( in != null )
        {
            serveResource( res, path, in );
        }
        else
        {
            res.sendError( HttpServletResponse.SC_NOT_FOUND );
        }
    }

    private void serveResource( final HttpServletResponse res, final String path, final InputStream in )
        throws IOException
    {
        String mimeType = "/".equals( path ) ? this.servletContext.getMimeType( INDEX_HTML ) : this.servletContext.getMimeType( path );
        // TODO .json was not resolved correctly from servletContext, maybe should be configured somewhere instead of using MediaTypes?
        if ( mimeType == null )
        {
            final MediaType mediaType = MediaTypes.instance().fromFile( path );
            if ( mediaType != null )
            {
                mimeType = mediaType.toString();
            }
        }
        res.setContentType( mimeType );
        ByteStreams.copy( in, res.getOutputStream() );
    }

    private InputStream findResource( final String path )
        throws IOException
    {
        if ( path.endsWith( "/" ) )
        {
            return findResource( path + INDEX_HTML );
        }

        final InputStream in = this.servletContext.getResourceAsStream( path );
        if ( in != null )
        {
            return in;
        }

        final String resourcePath = "/web" + ( path.startsWith( "/" ) ? path : ( "/" + path ) );
        final URL url = this.resourceLocator.findResource( resourcePath );

        if ( url == null )
        {
            return null;
        }

        return url.openStream();
    }
}
