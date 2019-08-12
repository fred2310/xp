package com.enonic.xp.lib.auditlog;

import com.enonic.xp.auditlog.AuditLogService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public abstract class BaseAuditLogHandler
    implements ScriptBean
{
    protected AuditLogService auditLogService;

    public final Object execute()
    {
        return this.doExecute();
    }

    protected abstract Object doExecute();

    @Override
    public void initialize( final BeanContext context )
    {
        this.auditLogService = context.getService( AuditLogService.class ).get();
    }
}
