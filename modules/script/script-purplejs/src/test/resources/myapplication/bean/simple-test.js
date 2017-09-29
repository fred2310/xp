var assert = Java.type('org.junit.Assert');

var bean1 = __.newBean('com.enonic.xp.script.impl.purplejs.bean.MyTestBeanOne');
assert.assertEquals('MyTestBeanOne', bean1.status);

var bean2 = __.newBean('com.enonic.xp.script.impl.purplejs.bean.MyTestBeanTwo');
assert.assertEquals('MyTestBeanTwo, myapplication:/bean/simple-test.js', bean2.status);
