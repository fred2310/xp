package com.enonic.xp.core.impl.project;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.*;
import com.enonic.xp.project.*;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.util.Exceptions;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component(immediate = true)
public class ProjectServiceImpl implements ProjectService
{
    private NodeService nodeService;

    private RepositoryService repositoryService;

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

        final List<Project> projects = result.getNodeIds().
                stream().
                map( nodeService::getById ).
                filter( Objects::nonNull ).
                map( this::toProject ).
                collect( Collectors.toList() );

        return Projects.from( projects );
    }

    @Override
    public Project get( ProjectName projectName )
    {
        return createContext().callWith( () -> doGet( projectName ) );
    }

    private Project doGet( final ProjectName name )
    {
        final Node node = nodeService.getByPath( toNodePath( name ) );
        return toProject( node );
    }

    @Override
    public Project create( CreateProjectParams params )
    {
        return createContext().callWith( () -> doCreate( params ) );
    }

    private Project doCreate( final CreateProjectParams params )
    {
        if ( nodeService.nodeExists( toNodePath( params.getName() ) ) )
        {
            throw new ProjectException( MessageFormat.format( "Project [{0}] already exists", params.getName() ) );
        }

        final PropertyTree data = toNodeData( params );
        final CreateNodeParams.Builder createNodeParams = CreateNodeParams.create().
                parent( ProjectConstants.PROJECT_PARENT_PATH ).
                name( params.getName().getValue() ).
                data( data ).
                nodeType( NodeType.from( ProjectConstants.NODE_TYPE ) ).
                inheritPermissions( true );
        if ( params.getIcon() != null )
        {
            createNodeParams.attachBinary( params.getIcon().getBinaryReference(), params.getIcon().getByteSource() );
        }
        final Node createdNode = nodeService.create( createNodeParams.build() );

        return toProject( createdNode );
    }

    @Override
    public Project modify( ModifyProjectParams params )
    {
        return createContext().callWith( () -> doModify( params ) );
    }

    private Project doModify( final ModifyProjectParams params )
    {
        if ( !nodeService.nodeExists( toNodePath( params.getName() ) ) )
        {
            throw new ProjectException( MessageFormat.format( "Project [{0}] does not exist", params.getName() ) );
        }

        //Updates node representation
        final UpdateNodeParams.Builder updateNodeParams = UpdateNodeParams.create().
                path( toNodePath( params.getName() ) ).
                editor( toBeEdited ->
                {
                    final PropertyTree data = toBeEdited.data;
                    data.setString( ProjectConstants.DISPLAY_NAME_PROPERTY_PATH, params.getDisplayName() );
                    data.setString( ProjectConstants.DESCRIPTION_PROPERTY_PATH, params.getDescription() );

                    final CreateAttachment icon = params.getIcon();
                    setIcon( icon, data );
                } );
        if ( params.getIcon() != null )
        {
            updateNodeParams.attachBinary( params.getIcon().getBinaryReference(), params.getIcon().getByteSource() );
        }
        final Node updatedNode = nodeService.update( updateNodeParams.build() );
        return toProject( updatedNode );
    }

    @Override
    public Project delete( ProjectName projectName )
    {
        return createContext().callWith( () -> doDelete( projectName ) );
    }

    private Project doDelete( final ProjectName name )
    {
        if ( ProjectName.DEFAULT_PROJECT_NAME.equals( name ) )
        {
            throw new ProjectException( "Default project cannot be deleted" );
        }
        final NodePath nodePath = toNodePath( name );
        final Node node = nodeService.getByPath( nodePath );
        if ( node == null )
        {
            throw new ProjectException( MessageFormat.format( "Project [{0}] does not exist", name ) );
        }

        nodeService.deleteById( node.id() );

        return toProject( node );
    }

    private PropertyTree toNodeData( final CreateProjectParams params )
    {
        PropertyTree data = new PropertyTree();
        data.setString( ProjectConstants.NAME_PROPERTY_PATH, params.getName().getValue() );
        data.setString( ProjectConstants.DISPLAY_NAME_PROPERTY_PATH, params.getDisplayName() );
        data.setString( ProjectConstants.DESCRIPTION_PROPERTY_PATH, params.getDescription() );

        final CreateAttachment icon = params.getIcon();
        setIcon( icon, data );

        return data;
    }

    private void setIcon( final CreateAttachment icon, final PropertyTree data )
    {
        if ( icon != null )
        {

            final PropertySet iconSet = data.newSet();
            data.setSet( ProjectConstants.ICON_PROPERTY_PATH.toString(), iconSet );
            iconSet.setString( ProjectConstants.ICON_NAME_PROPERTY_PATH, icon.getName() );
            iconSet.setString( ProjectConstants.ICON_LABEL_PROPERTY_PATH, icon.getLabel() );
            iconSet.setBinaryReference( "binary", icon.getBinaryReference() );
            iconSet.setString( ProjectConstants.ICON_MIMETYPE_PROPERTY_PATH, icon.getMimeType() );
            try
            {
                iconSet.setLong( ProjectConstants.ICON_SIZE_PROPERTY_PATH, icon.getByteSource().size() );
            } catch ( IOException e )
            {
                throw Exceptions.unchecked( e );
            }
        }
    }

    private NodePath toNodePath( final ProjectName name )
    {
        return NodePath.create( ProjectConstants.PROJECT_PARENT_PATH, name.getValue() ).build();
    }

    private Context createContext()
    {
        return ContextBuilder.from( ContextAccessor.current() ).
                branch( ContentConstants.BRANCH_MASTER ).
                build();
    }

    private Project toProject( final Node node )
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

    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }
}
