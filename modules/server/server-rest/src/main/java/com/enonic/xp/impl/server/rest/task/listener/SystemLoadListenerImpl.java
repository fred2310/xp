package com.enonic.xp.impl.server.rest.task.listener;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.SystemLoadListener;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.task.ProgressReporter;

public class SystemLoadListenerImpl
    implements SystemLoadListener
{
    private final ProgressReporter progressReporter;

    private int total = 0;

    private int progress = 0;

    private int currentBranchTotal = 0;

    private int currentBranchProgress = 0;

    public SystemLoadListenerImpl( final ProgressReporter progressReporter )
    {
        this.progressReporter = progressReporter;
    }

    @Override
    public void totalBranches( final long count )
    {
        total = Math.toIntExact( count );
        if ( progress > 0 )
        {
            progressReporter.progress( progress, this.total );
        }
    }

    @Override
    public void loadingBranch( final RepositoryId repositoryId, final Branch branch, final Long total )
    {
        currentBranchProgress = 0;
        currentBranchTotal = total.intValue();
    }


    @Override
    public void loadingVersions( final RepositoryId repositoryId )
    {
//        currentBranchTotal = 0;
//        currentBranchProgress = 0;
    }

    @Override
    public void entryLoaded()
    {
        currentBranchProgress++;

        if ( currentBranchTotal != 0 && total != 0 )
        {
            progress += currentBranchProgress / currentBranchTotal / total;
            progressReporter.progress( progress, total );
        }
    }
}
