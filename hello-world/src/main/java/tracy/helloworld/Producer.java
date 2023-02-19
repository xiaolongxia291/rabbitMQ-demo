package tracy.helloworld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class Producer {
    //队列名称
    public static final String QUEUE_NAME = "hello-world";

    //生产消息
    public static void main(String[] args) throws TimeoutException, IOException {
        //1 创建一个连接工厂
        ConnectionFactory factory=new ConnectionFactory();
        //2 设置工厂ip等信息 连接rabbitMQ
        factory.setHost("rabbitMQ的ip地址");
        factory.setPort(5673);//通信端口
        factory.setUsername("guest");//默认的用户名
        factory.setPassword("guest");//默认的密码

        //3 创建连接
        Connection connection=factory.newConnection();
        //4 获取信道
        Channel channel=connection.createChannel();

        //5 生成一个队列，参数挨个为：队列名，是否持久化，是否允许多消费者消费，其他参数
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        //6 发消息，参数挨个为：交换机，路由的key值，其他参数，发送的消息
        channel.basicPublish("",QUEUE_NAME,null,"hello world111".getBytes());
    }
}
