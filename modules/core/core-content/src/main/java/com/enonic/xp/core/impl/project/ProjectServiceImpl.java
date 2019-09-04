package com.enonic.xp.core.impl.project;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.*;
import com.enonic.xp.project.*;
import com.enonic.xp.query.filter.ValueFilter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component(immediate = true)
public class ProjectServiceImpl implements ProjectService
{
    private NodeService nodeService;

    @Override
    public Projects list()
    {
        return createContext().callWith( this::doList );
    }

    private Projects doList()
    {
        final ValueFilter valueFilter = ValueFilter.create().
                fieldName( NodeIndexPath.NODE_TYPE.getPath() ).
                addValue( ValueFactory.newString( ProjectConstants.NODE_TYPE ) ).
                build();

        final NodeQuery nodeQuery = NodeQuery.create().
                addQueryFilter( valueFilter ).
                size( 1024 ).
                build();

        final FindNodesByQueryResult result = nodeService.findByQuery( nodeQuery );

        final List<Project> contentLayers = result.getNodeIds().
                stream().
                map( nodeService::getById ).
                filter( Objects::nonNull ).
                map( this::toContentLayer ).
                collect( Collectors.toList() );

        return Projects.from( contentLayers );
    }

    @Override
    public Project get( ProjectName projectName )
    {
        return null;
    }

    @Override
    public void create( CreateProjectParams params )
    {

    }

    @Override
    public void modify( ModifyProjectParams params )
    {

    }

    @Override
    public void delete( ProjectName projectName )
    {

    }


    private Context createContext()
    {
        return ContextBuilder.from( ContextAccessor.current() ).
                branch( ContentConstants.BRANCH_MASTER ).
                build();
    }

    private Project toContentLayer( final Node node )
    {
        if ( node == null )
        {
            return null;
        }
        final String name = node.data().getString( ProjectConstants.NAME_PROPERTY_PATH );
        final String displayName = node.data().getString( ProjectConstants.DISPLAY_NAME_PROPERTY_PATH );
        final String description = node.data().getString( ProjectConstants.DESCRIPTION_PROPERTY_PATH );
        final PropertySet iconSet = node.data().getSet( ProjectConstants.ICON_PROPERTY_PATH );
        return Project.create().
                name( ProjectName.from( name ) ).
                displayName( displayName ).
                description( description ).
                icon( toAttachment( iconSet ) ).
                build();
    }

    private Attachment toAttachment( final PropertySet set )
    {
        if ( set != null )
        {
            return Attachment.create().
                    name( set.getString( ProjectConstants.ICON_NAME_PROPERTY_PATH ) ).
                    label( ProjectConstants.ICON_LABEL_VALUE ).
                    mimeType( set.getString( ProjectConstants.ICON_MIMETYPE_PROPERTY_PATH ) ).
                    size( set.getLong( ProjectConstants.ICON_SIZE_PROPERTY_PATH ) ).
                    build();
        }
        return null;
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

}
