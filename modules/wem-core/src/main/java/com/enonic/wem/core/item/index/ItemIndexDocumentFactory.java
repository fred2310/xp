package com.enonic.wem.core.item.index;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertyVisitor;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemIndexConfig;
import com.enonic.wem.api.item.PropertyIndexConfig;
import com.enonic.wem.core.index.IndexConstants;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.indexdocument.IndexDocument2;
import com.enonic.wem.core.index.indexdocument.IndexDocumentItemFactory;


public class ItemIndexDocumentFactory
{
    protected static final String CREATED_TIME_PROPERTY_NAME = "createdTime";

    protected static final String CREATOR_PROPERTY_NAME = "creator";

    protected static final String MODIFIED_TIME_PROPERTY_NAME = "modifiedTime";

    protected static final String MODIFIER_PROPERTY_NAME = "modifier";

    protected static final String PATH_PROPERTY_NAME = "path";

    private static final PropertyIndexConfig metadataPropertyIndexConfig = PropertyIndexConfig.newPropertyIndexConfig().
        enabled( true ).
        autocompleteEnabled( false ).
        fulltextEnabled( false ).
        build();

    private static final PropertyIndexConfig defaultPropertyIndexConfig = PropertyIndexConfig.newPropertyIndexConfig().
        enabled( true ).
        autocompleteEnabled( true ).
        fulltextEnabled( true ).
        build();

    public Collection<IndexDocument2> create( final Item item )
    {
        Set<IndexDocument2> indexDocuments = Sets.newHashSet();

        indexDocuments.add( createDataDocument( item ) );

        return indexDocuments;
    }

    private IndexDocument2 createDataDocument( final Item item )
    {
        final ItemIndexConfig itemIndexConfig = item.getItemIndexConfig();

        final IndexDocument2.Builder builder = IndexDocument2.newIndexDocument().
            id( item.id() ).
            index( IndexConstants.NODB_INDEX ).
            indexType( IndexType.NODE ).
            analyzer( itemIndexConfig.getAnalyzer() );

        addItemMetaData( item, builder );
        addItemData( item, builder );

        return builder.build();
    }

    private void addItemMetaData( final Item item, final IndexDocument2.Builder builder )
    {
        // TODO: Add the rest of the metadata

        builder.addEntries( IndexDocumentItemFactory.create( CREATED_TIME_PROPERTY_NAME, new Value.DateTime( item.getCreatedTime() ),
                                                             metadataPropertyIndexConfig ) );

        builder.addEntries( IndexDocumentItemFactory.create( PATH_PROPERTY_NAME, new Value.String( item.path().toString() ),
                                                             metadataPropertyIndexConfig ) );

        if ( item.getCreator() != null )
        {
            builder.addEntries(
                IndexDocumentItemFactory.create( CREATOR_PROPERTY_NAME, new Value.String( item.getCreator().getQualifiedName() ),
                                                 metadataPropertyIndexConfig ) );
        }

        if ( item.getModifiedTime() != null )
        {
            builder.addEntries( IndexDocumentItemFactory.create( MODIFIED_TIME_PROPERTY_NAME, new Value.DateTime( item.getModifiedTime() ),
                                                                 metadataPropertyIndexConfig ) );
        }

        if ( item.getModifier() != null )
        {
            builder.addEntries(
                IndexDocumentItemFactory.create( MODIFIER_PROPERTY_NAME, new Value.String( item.getModifier().getQualifiedName() ),
                                                 metadataPropertyIndexConfig ) );
        }

    }

    private void addItemData( final Item item, final IndexDocument2.Builder builder )
    {
        PropertyVisitor visitor = new PropertyVisitor()
        {
            @Override
            public void visit( final Property property )
            {
                PropertyIndexConfig propertyIndexConfig = item.getItemIndexConfig().getPropertyIndexConfig( property.getPath() );

                if ( propertyIndexConfig == null )
                {
                    propertyIndexConfig = defaultPropertyIndexConfig;
                }

                builder.addEntries( IndexDocumentItemFactory.create( property, propertyIndexConfig ) );
            }
        };

        visitor.traverse( item.rootDataSet() );
    }


}