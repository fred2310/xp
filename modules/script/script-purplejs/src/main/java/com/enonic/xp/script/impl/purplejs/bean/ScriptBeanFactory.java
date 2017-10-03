package com.enonic.xp.script.impl.purplejs.bean;

public interface ScriptBeanFactory
{
    Object newBean( String type )
        throws Exception;
}
