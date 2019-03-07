package com.enonic.xp.impl.task.cluster;

import java.util.List;

import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportException;
import org.elasticsearch.transport.TransportResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.util.Exceptions;

public class TaskTransportResponseHandler
    implements TransportResponseHandler<TaskTransportResponse>
{
    private final static Logger LOG = LoggerFactory.getLogger( TaskTransportRequestSenderImpl.class );

    //    private static final long THREAD_TIMEOUT = TaskTransportRequestSenderImpl.TRANSPORT_REQUEST_TIMEOUT + 1_000l;
    private static final long THREAD_TIMEOUT = Long.parseLong( System.getenv( "lerunar2" ) );


    private final ImmutableList.Builder<TaskInfo> taskInfos = ImmutableList.builder();

    private int awaitingResponseCount;

    private TransportException transportException;

    public TaskTransportResponseHandler( final int responseCount )
    {
        Preconditions.checkArgument( responseCount > 0, "responseCount must be greater than 0" );
        this.awaitingResponseCount = responseCount;
    }


    @Override
    public TaskTransportResponse newInstance()
    {
        return new TaskTransportResponse();
    }

    @Override
    public synchronized void handleResponse( final TaskTransportResponse response )
    {
        LOG.info( "TaskTransportResponseHandler:handleResponse" );
        taskInfos.addAll( response.getTaskInfos() );
        awaitingResponseCount--;
        this.notifyAll();
    }

    @Override
    public synchronized void handleException( final TransportException e )
    {
        LOG.info( "TaskTransportResponseHandler:handleException" );
        transportException = e;
        this.notifyAll();
    }

    @Override
    public String executor()
    {
        return ThreadPool.Names.MANAGEMENT;
    }

    public synchronized List<TaskInfo> getTaskInfos()
    {
        LOG.info( "TaskTransportResponseHandler:getTaskInfos / Begin" );
        final long startTime = System.currentTimeMillis();
        while ( transportException == null && awaitingResponseCount > 0 )
        {
            long deltaTime = System.currentTimeMillis() - startTime;
            if ( deltaTime >= THREAD_TIMEOUT )
            {
                //Should never happen. An ES timeout exception should have been handled
                throw new TaskTransportTimeoutException( deltaTime );
            }

            try
            {
                LOG.info( "TaskTransportResponseHandler:getTaskInfos / Wait" + THREAD_TIMEOUT );
                this.wait( THREAD_TIMEOUT );
            }
            catch ( InterruptedException e )
            {
                throw Exceptions.unchecked( e );
            }
        }

        if ( transportException != null )
        {
            throw transportException;
        }
        LOG.info( "TaskTransportResponseHandler:getTaskInfos / End" );
        return taskInfos.build();


    }
}
