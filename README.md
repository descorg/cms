# cms
内容管理系统

# cms-api
## cms-rabbit-api
RabbitMQ独立公共模块。
生产者实例：
1.Application入口文件需要增加下面注解

@ComponentScan(basePackages = {"org.springcms.rabbit.core", "org.springcms.demo"})
@MapperScan(basePackages = {"org.springcms.rabbit.core.mapper"})

2.发送

    @Resource
    private RabbitMqUtils rabbitMqUtils;

    @GetMapping("push")
    @ApiOperation(value = "推送消息")
    public R<Boolean> push(@RequestParam String source, @RequestParam String content) {
        //创建队列
        rabbitMqUtils.create(source);
        //发送消息
        rabbitMqUtils.push(source, content);
        return R.success("ok");
    }

消费者实例，需要增加监听事件。如下

    @Component
    @Description("监听队列")
    public class ErrorEvent extends RabbitMessageEvent {
    
        @Override
        @RabbitListener(bindings = @QueueBinding(
                value = @Queue(value = "topic.queue.error.integral"),
                exchange = @Exchange(value = "topic.queue.exchange.error", type = ExchangeTypes.TOPIC),
                key = "topic.queue.error.update"
        ))
        public void onMessage(String content) {
            super.onMessage(content);
        }
    
        @Override
        public void execute(String content) {
    
        }
    }



# cms-common
## cms-core
公共库

## cms-core-launch
自定义启动器

## cms-core-mongo
MongoDb独立操作库

## cms-core-mybatis
数据库操作基础库

## cms-jwt
jwt

## cms-redis
Redis独立模块

# cms-flow
flowable工作流

# cms-gateway
网关
gateway + nacos + hystrix + openfeign

# cms-rabbit
Rabbit模块，查询收发记录

参考: 

https://blog.csdn.net/AI_STUDENT_QYB/article/details/121121715
https://blog.csdn.net/qq_38380025/article/details/102968559


# cms-demo
演示程序