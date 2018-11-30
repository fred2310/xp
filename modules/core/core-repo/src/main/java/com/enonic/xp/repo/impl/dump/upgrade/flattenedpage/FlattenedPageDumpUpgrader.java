package com.enonic.xp.repo.impl.dump.upgrade.flattenedpage;

import java.io.IOException;
import java.nio.file.Path;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

import com.enonic.xp.blob.Segment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.dump.DumpBlobRecord;
import com.enonic.xp.repo.impl.dump.reader.FileDumpReader;
import com.enonic.xp.repo.impl.dump.upgrade.DumpUpgradeException;
import com.enonic.xp.repo.impl.dump.upgrade.DumpUpgrader;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.node.json.NodeVersionJsonSerializer;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.util.Version;

public class FlattenedPageDumpUpgrader
    implements DumpUpgrader
{
    private final Path basePath;

    private FileDumpReader dumpReader;

    private final NodeVersionJsonSerializer serializer = NodeVersionJsonSerializer.create( false );

    public FlattenedPageDumpUpgrader( final Path basePath )
    {
        this.basePath = basePath;
    }

    @Override
    public Version getModelVersion()
    {
        return new Version( 3, 0, 0 );
    }

    @Override
    public void upgrade( final String dumpName )
    {
        this.dumpReader = new FileDumpReader( basePath, dumpName, null );
        final Segment segment = RepositorySegmentUtils.toSegment( ContentConstants.CONTENT_REPO_ID, NodeConstants.NODE_SEGMENT_LEVEL );
        dumpReader.processDumpBlobRecord( this::upgradeBlobRecord, segment );
    }

    private void upgradeBlobRecord( final DumpBlobRecord dumpBlobRecord )
    {
        final NodeVersion nodeVersion = getNodeVersion( dumpBlobRecord );
        final boolean upgraded = new FlattenedPageDataUpgrader().upgrade( nodeVersion.getData() );
        if ( upgraded )
        {
            writeNodeVersion( nodeVersion, dumpBlobRecord );
        }
    }

    private NodeVersion getNodeVersion( final DumpBlobRecord dumpBlobRecord )
    {
        final CharSource charSource = dumpBlobRecord.getBytes().asCharSource( Charsets.UTF_8 );
        try
        {
            return serializer.toNodeVersion( charSource.read() );
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot read node version [" + dumpBlobRecord.getKey() + "]", e );
        }
    }

    private void writeNodeVersion( final NodeVersion nodeVersion, final DumpBlobRecord dumpBlobRecord )
    {
        final String serializedUpgradedNodeVersion = serializer.toString( nodeVersion );
        final ByteSource byteSource = ByteSource.wrap( serializedUpgradedNodeVersion.getBytes( Charsets.UTF_8 ) );
        try
        {

            byteSource.copyTo( dumpBlobRecord.getByteSink() );
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot read node version [" + dumpBlobRecord.getKey() + "]", e );
        }
    }


}
