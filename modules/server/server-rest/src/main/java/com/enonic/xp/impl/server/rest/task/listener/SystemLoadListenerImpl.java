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

    private int current = 0;

    public SystemLoadListenerImpl( final ProgressReporter progressReporter )
    {
        this.progressReporter = progressReporter;
    }

    @Override
    public void totalBranches( final long count )
    {
        total = Math.toIntExact( count );
        if ( current > 0 )
        {
            progressReporter.progress( current, this.total );
        }
    }

    @Override
    public void branchLoaded()
    {
        if ( total > 0 )
        {
            progressReporter.progress( ++current, this.total );
        }
        else
        {
            current++;
        }
    }

    @Override
    public void loadingBranch( final RepositoryId repositoryId, final Branch branch, final Long total )
    {
    }


    @Override
    public void loadingVersions( final RepositoryId repositoryId )
    {
    }

    @Override
    public void entryLoaded()
    {
    }
}
