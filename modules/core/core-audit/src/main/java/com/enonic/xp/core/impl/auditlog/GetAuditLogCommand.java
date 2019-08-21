package com.enonic.xp.core.impl.auditlog;

import com.google.common.base.Preconditions;

import com.enonic.xp.auditlog.AuditLog;
import com.enonic.xp.auditlog.AuditLogId;
import com.enonic.xp.core.impl.auditlog.serializer.AuditLogSerializer;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;

public class GetAuditLogCommand
    extends NodeServiceCommand<AuditLog>
{
    private final AuditLogId auditLogId;

    private GetAuditLogCommand( final Builder builder )
    {
        super( builder );
        auditLogId = builder.auditLogId;
    }

    @Override
    public AuditLog execute()
    {
        final NodeId nodeId = NodeId.from( auditLogId.toString() );
        Node node = AuditLogContext.createContext().callWith( () -> nodeService.getById( nodeId ) );
        return AuditLogSerializer.fromNode( node );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends NodeServiceCommand.Builder<Builder>
    {
        private AuditLogId auditLogId;

        private Builder()
        {
        }

        public Builder auditLogId( final AuditLogId val )
        {
            auditLogId = val;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( auditLogId, "AuditLogId cannot be null" );
        }

        public GetAuditLogCommand build()
        {
            validate();
            return new GetAuditLogCommand( this );
        }
    }
}
