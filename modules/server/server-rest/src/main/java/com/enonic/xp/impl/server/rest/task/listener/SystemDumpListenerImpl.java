package com.enonic.xp.impl.server.rest.task.listener;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.SystemDumpListener;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.task.ProgressReporter;

public class SystemDumpListenerImpl
    implements SystemDumpListener
{
    private final ProgressReporter progressReporter;

    private int total = 0;

    private int progressCount = 0;

    public SystemDumpListenerImpl( final ProgressReporter progressReporter )
    {
        this.progressReporter = progressReporter;
    }

    @Override
    public void totalBranches( final long total )
    {
        this.total = Math.toIntExact( total );
    }

    @Override
    public void dumpingBranch( final RepositoryId repositoryId, final Branch branch, final long total )
    {
    }

    @Override
    public void nodeDumped()
    {
    }

    @Override
    public void branchDumped()
    {
        progressReporter.progress( ++progressCount, this.total );
    }
}
