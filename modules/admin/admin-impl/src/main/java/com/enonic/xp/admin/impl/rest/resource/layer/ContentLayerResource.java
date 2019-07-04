package com.enonic.xp.admin.impl.rest.resource.layer;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.layer.ContentLayer;
import com.enonic.xp.layer.ContentLayerName;
import com.enonic.xp.layer.ContentLayerService;
import com.enonic.xp.security.RoleKeys;

@Path(ResourceConstants.REST_ROOT + "layer")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({RoleKeys.ADMIN_LOGIN_ID, RoleKeys.ADMIN_ID})
@Component(immediate = true, property = "group=admin")
public class ContentLayerResource
    implements JaxRsComponent
{
    private ContentLayerService contentLayerService;

    @GET
    @Path("list")
    public List<ContentLayerJson> list()
    {
        return contentLayerService.list().
            stream().
            map( contentLayer -> new ContentLayerJson( contentLayer ) ).
            collect( Collectors.toList() );
    }

    @GET
    @Path("get")
    public ContentLayerJson get( @QueryParam("name") final String name )
    {
        final ContentLayer contentLayer = contentLayerService.get( name == null ? null : ContentLayerName.from( name ) );
        return contentLayer == null ? null : new ContentLayerJson( contentLayer );
    }

    @POST
    @Path("create")
    public ContentLayerJson create( final CreateLayerJson params )
    {
        final ContentLayer contentLayer = contentLayerService.create( params.getParams() );
        return new ContentLayerJson( contentLayer );
    }

    @Reference
    public void setContentLayerService( final ContentLayerService contentLayerService )
    {
        this.contentLayerService = contentLayerService;
    }
}