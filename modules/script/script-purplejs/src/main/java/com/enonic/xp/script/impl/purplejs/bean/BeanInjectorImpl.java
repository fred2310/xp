package com.enonic.xp.script.impl.purplejs.bean;

import io.purplejs.core.inject.BeanInjector;
import io.purplejs.core.inject.InjectorContext;
import io.purplejs.core.resource.ResourcePath;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.script.impl.purplejs.executor.ScriptExecutor;

public final class BeanInjectorImpl
    implements BeanInjector
{
    private final ScriptExecutor executor;

    public BeanInjectorImpl( final ScriptExecutor executor )
    {
        this.executor = executor;
    }

    @Override
    public void inject( final InjectorContext context )
    {
        final Object instance = context.getInstance();
        if ( instance instanceof ScriptBean )
        {
            injectBean( (ScriptBean) instance, context.getResource() );
        }
    }

    private void injectBean( final ScriptBean scriptBean, final ResourcePath resource )
    {
        final ResourceKey resourceKey = ResourceKey.from( this.executor.getApplication().getKey(), resource.getPath() );
        final BeanContextImpl beanContext = new BeanContextImpl();
        beanContext.setExecutor( this.executor );
        beanContext.setResourceKey( resourceKey );

        scriptBean.initialize( beanContext );
    }
}
