package com.enonic.xp.repo.impl.dump.serializer;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;
import com.enonic.xp.repo.impl.dump.model.CommitDumpEntry;
import com.enonic.xp.repo.impl.dump.model.VersionsDumpEntry;

public interface DumpSerializer
{
    String serialize( final BranchDumpEntry branchDumpEntry );

    String serialize( final VersionsDumpEntry versionsDumpEntry );

    String serialize( final CommitDumpEntry commitDumpEntry );

    String serialize( final PropertyTree propertyTree );

    BranchDumpEntry toBranchMetaEntry( final String value );

    VersionsDumpEntry toNodeVersionsEntry( final String value );

    CommitDumpEntry toCommitDumpEntry( final String value );

    PropertyTree toPropertyTree( String value );
}
