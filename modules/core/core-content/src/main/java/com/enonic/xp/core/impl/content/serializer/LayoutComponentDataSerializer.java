package com.enonic.xp.core.impl.content.serializer;


import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutComponentType;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.LayoutRegions;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.Regions;

final class LayoutComponentDataSerializer
    extends DescriptorBasedComponentDataSerializer<LayoutComponent>
{
    private final RegionDataSerializer regionDataSerializer;

    public LayoutComponentDataSerializer( final RegionDataSerializer regionDataSerializer )
    {
        this.regionDataSerializer = regionDataSerializer;
    }

    @Override
    public void toData( final LayoutComponent component, final PropertySet parent )
    {
        super.toData( component, parent );

        if ( component.hasRegions() )
        {
            for ( final Region region : component.getRegions() )
            {
                regionDataSerializer.toData( region, parent );
            }
        }
    }

    @Override
    public LayoutComponent fromData( final PropertySet data )
    {
        return fromData( data, new ArrayList<>() );
    }

    public LayoutComponent fromData( final PropertySet layoutData, final List<PropertySet> componentsAsData )
    {
        final LayoutComponent.Builder layoutComponent = LayoutComponent.create();

        final LayoutRegions.Builder layoutRegionsBuilder = LayoutRegions.create();

        final PropertySet specialBlockSet = layoutData.getSet( LayoutComponentType.INSTANCE.toString() );

        if ( specialBlockSet != null && specialBlockSet.isNotNull( DESCRIPTOR ) )
        {
            final DescriptorKey descriptorKey = DescriptorKey.from( specialBlockSet.getString( DESCRIPTOR ) );

            layoutComponent.descriptor( descriptorKey );
            layoutComponent.config( getConfigFromData( specialBlockSet, descriptorKey ) );

            final String layoutPath = layoutData.getString( PATH );
            regionDataSerializer.fromData( layoutPath, componentsAsData ).
                forEach( layoutRegionsBuilder::add );
        }

        layoutComponent.regions( layoutRegionsBuilder.build() );

        return layoutComponent.build();
    }
}
