###服务器
##序号

1.模拟RequestMapping方法（当前只实现到controller顶层）  20190408
启动初始化时存map<path，形参类型>，访问时从request里获取到path，再根据获取到的path去map里获取到形参类型，将request转型成对应的形参类型，通过eventbus或者disruptor发布即可执行对应方法。最后返回结果。
缺点：每个方法的形参类型不能重复。
