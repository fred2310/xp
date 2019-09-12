package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;

import com.google.common.io.LineProcessor;

import com.enonic.xp.data.PropertyTree;

public class RepositoryDataEntryProcessor
    extends AbstractEntryProcessor
    implements LineProcessor<EntryLoadResult>
{
    private EntryLoadResult result;

    private RepositoryDataEntryProcessor( final RepositoryDataEntryProcessor.Builder builder )
    {
        super( builder );
    }

    @Override
    public boolean processLine( final String line )
        throws IOException
    {
        final EntryLoadResult.Builder result = EntryLoadResult.create();

        final PropertyTree data = this.serializer.toPropertyTree( line );
        this.nodeService
        result.successful();

        this.result = result.build();
        return true;
    }

    @Override
    public EntryLoadResult getResult()
    {
        return result;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractEntryProcessor.Builder<Builder>
    {
        public RepositoryDataEntryProcessor build()
        {
            return new RepositoryDataEntryProcessor( this );
        }
    }


}
