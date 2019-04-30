package com.enonic.xp.core.impl.content.serializer;


import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.core.impl.content.page.region.ComponentTypes;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.region.ComponentType;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.Regions;

import static com.enonic.xp.core.impl.content.serializer.ComponentDataSerializer.PATH;
import static com.enonic.xp.core.impl.content.serializer.ComponentDataSerializer.TYPE;

final class RegionDataSerializer
{
    private final ComponentDataSerializerProvider componentDataSerializerProvider;

    public RegionDataSerializer( final ComponentDataSerializerProvider componentDataSerializerProvider )
    {
        this.componentDataSerializerProvider = componentDataSerializerProvider;
    }

    public void toData( final Region region, final PropertySet parent )
    {
        for ( final Component component : region.getComponents() )
        {
            componentDataSerializerProvider.getDataSerializer( component.getType() ).toData( component, parent );
        }
    }

    public Regions fromData( final String parentPath, final List<PropertySet> componentsAsData )
    {
        final HashMap<String, Region.Builder> regionMap = new HashMap<>();

        final String prefix = ( ComponentPath.DIVIDER.equals( parentPath ) ? "" : parentPath ) + ComponentPath.DIVIDER;
        final long componentLevel = ( ComponentPath.DIVIDER.equals( parentPath ) ? 0 : getComponentPathDepth( parentPath ) ) + 2;

        componentsAsData.stream().filter( componentAsData -> {
            final String componentPath = componentAsData.getString( PATH );
            return componentPath.startsWith( prefix ) && getComponentPathDepth( componentPath ) == componentLevel;
        } ).
            forEach( componentAsData -> {
                final String componentPath = componentAsData.getString( PATH );
                final String[] componentPathArray = componentPath.split( ComponentPath.DIVIDER );
                final String regionName = componentPathArray[componentPathArray.length - 2];
                final Component component = getComponent( componentAsData, componentsAsData );
                Region.Builder region = regionMap.get( regionName );
                if ( region == null )
                {
                    region = Region.create().name( regionName );
                    regionMap.put( regionName, region );
                }
                region.add( component );
            } );

        final List<Region> regionList = regionMap.values().
            stream().
            map( Region.Builder::build ).
            collect( Collectors.toList() );
        return Regions.from( regionList );
    }

    private long getComponentPathDepth( final String componentPath )
    {
        return componentPath.chars().filter( ch -> ch == '/' ).count();
    }

    Component getComponent( final PropertySet componentData, final List<PropertySet> componentsAsData )
    {
        final ComponentType type = ComponentTypes.byShortName( componentData.getString( TYPE ) );
        final ComponentDataSerializer componentDataSerializer = componentDataSerializerProvider.getDataSerializer( type );

        if ( componentDataSerializer instanceof LayoutComponentDataSerializer )
        {
            return ( (LayoutComponentDataSerializer) componentDataSerializer ).fromData( componentData, getDescendantsOf( componentData,
                                                                                                                          componentsAsData ) );
        }

        return componentDataSerializer.fromData( componentData );
    }

    private List<PropertySet> getDescendantsOf( final PropertySet item, final List<PropertySet> componentsAsData )
    {
        final String parentPath = item.getString( PATH );

        return componentsAsData.stream().filter( componentAsData -> isItemDescendantOf( componentAsData, parentPath ) ).collect(
            Collectors.toList() );
    }

    private boolean isItemDescendantOf( final PropertySet item, final String parentPath )
    {
        final String itemPath = item.getString( PATH );

        return itemPath.startsWith( parentPath );
    }
}
