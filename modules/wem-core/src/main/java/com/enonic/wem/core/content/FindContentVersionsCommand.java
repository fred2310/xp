package com.enonic.wem.core.content;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentVersions;
import com.enonic.wem.api.content.FindContentVersionsResult;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.FindNodeVersionsResult;
import com.enonic.wem.api.entity.GetNodeVersionsParams;

public class FindContentVersionsCommand
    extends AbstractContentCommand
{
    private final ContentId contentId;

    private final int from;

    private final int size;

    private FindContentVersionsCommand( final Builder builder )
    {
        super( builder );
        contentId = builder.contentId;
        from = builder.from;
        size = builder.size;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public FindContentVersionsResult execute()
    {
        return doGetContentVersions();
    }

    private FindContentVersionsResult doGetContentVersions()
    {
        final EntityId entityId = EntityId.from( this.contentId );

        final FindNodeVersionsResult findNodeVersionsResult = nodeService.getVersions( GetNodeVersionsParams.create().
            entityId( entityId ).
            from( this.from ).
            size( this.size ).
            build(), this.context );

        final FindContentVersionsResult.Builder findContentVersionsResultBuilder = FindContentVersionsResult.create();
        findContentVersionsResultBuilder.hits( findNodeVersionsResult.getHits() );
        findContentVersionsResultBuilder.totalHits( findNodeVersionsResult.getTotalHits() );
        findContentVersionsResultBuilder.from( findNodeVersionsResult.getFrom() );
        findContentVersionsResultBuilder.size( findNodeVersionsResult.getSize() );

        final ContentVersionFactory contentVersionFactory = new ContentVersionFactory( this.translator, this.nodeService, this.context );

        final ContentVersions contentVersions = contentVersionFactory.create( entityId, findNodeVersionsResult.getNodeVersions() );

        findContentVersionsResultBuilder.contentVersions( contentVersions );

        return findContentVersionsResultBuilder.build();
    }


    public static final class Builder
        extends AbstractContentCommand.Builder<Builder>

    {
        private ContentId contentId;

        private int from;

        private int size;

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder from( int from )
        {
            this.from = from;
            return this;
        }

        public Builder size( int size )
        {
            this.size = size;
            return this;
        }

        public FindContentVersionsCommand build()
        {
            return new FindContentVersionsCommand( this );
        }
    }
}
