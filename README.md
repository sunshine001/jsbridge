# jsapibridge

###jsapibridge是一个方法映射中间件。
在程序编译过程中，jsapibridge通过abstractProcessor获取所有被JSApi或JSApiError注解的方法，
通过javaPoet技术动态生成Api代理类JSApiProxy.java。

###与反射的比较：
注解动态生成源码运行效率与普通JAVA类运行是一样的，而JAVA反射在运行时因为需要进行类初始化、参数合法性校验等检查导致运行效率较低。<br/>
目前主流的开源框架如EventBus、otto、BufferKnife都采用了注解动态生成源码方式实现。本方案在实现过程中参考了部分BufferKinfe原理。


