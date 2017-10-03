package com.enonic.xp.script.impl.purplejs.function;

import com.enonic.xp.app.Application;
import com.enonic.xp.script.impl.purplejs.bean.BeanContextImpl;
import com.enonic.xp.script.impl.purplejs.bean.ScriptBeanFactory;
import com.enonic.xp.script.impl.purplejs.bean.ScriptBeanFactoryImpl;
import com.enonic.xp.script.impl.purplejs.executor.ScriptExecutor;


public final class ScriptFunctions
{
    private final ScriptExecutor executor;

    private final ScriptBeanFactory scriptBeanFactory;
//
//    private final JsObjectConverter converter;
//
//    private final ScriptLogger logger;

    public ScriptFunctions( final ScriptExecutor executor )
    {
//        this.script = script;
        this.executor = executor;

        final BeanContextImpl beanContext = new BeanContextImpl();
        beanContext.setExecutor( this.executor );
//        beanContext.setResourceKey( this.script ); // TODO
//
        this.scriptBeanFactory = new ScriptBeanFactoryImpl( this.executor.getApplication().getClassLoader(), beanContext );
//        this.converter = new JsObjectConverter( this.executor.getJavascriptHelper() );
//        this.logger = new ScriptLogger( this.script, this.executor.getJavascriptHelper() );
    }

//    public ScriptLogger getLog()
//    {
//        return this.logger;
//    }

    public Application getApp()
    {
        return this.executor.getApplication();
    }

    public Object newBean( final String type )
        throws Exception
    {
        return this.scriptBeanFactory.newBean( type );
    }

    //    public ScriptValue toScriptValue( final Object value )
//    {
//        return this.executor.newScriptValue( value );
//    }
//
    public Object toNativeObject( final Object value )
    {
//        return this.converter.toJs( value );
        return value;// TODO
    }
//
//    public Object nullOrValue( final Object value )
//    {
//        return NashornHelper.isUndefined( value ) ? null : value;
//    }
//
//    public void registerMock( final String name, final Object value )
//    {
//        this.executor.registerMock( name, value );
//    }
//
//    public void disposer( final Runnable runnable )
//    {
//        this.executor.registerDisposer( this.script, runnable );
//    }
}
