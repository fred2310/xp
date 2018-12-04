package com.enonic.xp.impl.server.rest.task;

import java.nio.file.Paths;

import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.SystemDumpParams;
import com.enonic.xp.home.HomeDir;
import com.enonic.xp.impl.server.rest.model.SystemDumpRequestJson;
import com.enonic.xp.task.AbstractRunnableTask;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;

public class DumpRunnableTask
    extends AbstractRunnableTask
{
    private final SystemDumpRequestJson params;

    private final DumpService dumpService;

    private DumpRunnableTask( Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.dumpService = builder.dumpService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final SystemDumpParams systemDumpParams = SystemDumpParams.create().
            dumpName( params.getName() ).
            includeBinaries( true ).
            includeVersions( params.isIncludeVersions() ).
            maxAge( params.getMaxAge() ).
            maxVersions( params.getMaxVersions() ).
            build();

        this.dumpService.dump( systemDumpParams );
    }

    private java.nio.file.Path getExportDirectory( final String exportName )
    {
        return Paths.get( HomeDir.get().toString(), "data", "export", exportName ).toAbsolutePath();
    }

    public static class Builder
        extends AbstractRunnableTask.Builder<Builder>
    {
        private SystemDumpRequestJson params;

        private DumpService dumpService;

        public Builder params( SystemDumpRequestJson params )
        {
            this.params = params;
            return this;
        }

        public Builder dumpService( final DumpService dumpService )
        {
            this.dumpService = dumpService;
            return this;
        }

        public DumpRunnableTask build()
        {
            return new DumpRunnableTask( this );
        }
    }
}
