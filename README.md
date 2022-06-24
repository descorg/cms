# cms
内容管理系统

# cms-api
## cms-rabbit-api
RabbitMQ独立公共模块。

引入模块

        <dependency>
            <groupId>org.springcms</groupId>
            <artifactId>cms-rabbit-api</artifactId>
            <version>1.1.1.RELEASE</version>
        </dependency>

修改配置文件

    spring:
        rabbitmq:
            host: 127.0.0.1
            port: 5672
            username: guest
            password: guest
        datasource:
            driver-class-name: com.mysql.cj.jdbc.Driver
            url: jdbc:mysql://127.0.0.1:3306/crm?useSSL=false&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&tinyInt1isBit=false&allowMultiQueries=true&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
            username: root
            password: 123456

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

消费者实例，需要增加监听事件。例一：

    @Component
    @Description("监听队列")
    public class MyRabbitEvent extends AbstractMessageEvent {
    
        @Override
        @RabbitListener(bindings = @QueueBinding(
                value = @Queue(value = "topic.queue.order.integral"),
                exchange = @Exchange(value = "topic.queue.exchange.order", type = ExchangeTypes.TOPIC),
                key = "topic.queue.order.update"
        ))
        public void onMessage(String content) {
            super.onMessage(content);
        }
    
        @Override
        public void execute(String content) {
    
        }
    }


例二：

修改配置文件

    rabbit:
        listener:
            sender: order
            receive: integral

监听类

    import org.springcms.rabbit.core.event.SingleMessageEvent;
    import org.springframework.stereotype.Component;
    
    @Component
    public class MyRabbitEvent extends SingleMessageEvent {
        @Override
        public void execute(String content) {
            System.out.println(content);
        }
    }


# cms-common
## cms-core
公共库

## cms-core-launch
自定义启动器，启动命令改为

    CmsApplication.run("cms-develop", DevelopApplication.class, args);

## cms-core-mongo
MongoDb独立操作库，需要增加配置

    spring:
        data:
        mongodb:
            uri: mongodb://localhost:27017/test
            database: test

## cms-core-mybatis
数据库操作基础库

## cms-jwt
jwt

## cms-redis
Redis独立模块

# cms-develop
开发者工具

生成的代码所属模块，启动程序需要增加bean的扫描路径，如下所示

    @SpringBootApplication(scanBasePackages = {"org.springcms.core"})

# cms-flow
flowable工作流

# cms-gateway
网关
gateway + nacos + hystrix + openfeign

# cms-rabbit
Rabbit模块，查询收发记录

# cms-report
报表工具


参考: 

https://blog.csdn.net/AI_STUDENT_QYB/article/details/121121715
https://blog.csdn.net/qq_38380025/article/details/102968559


# cms-demo
演示程序