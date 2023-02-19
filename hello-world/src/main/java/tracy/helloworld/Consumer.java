package tracy.helloworld;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class Consumer {
    //队列名称
    public static final String QUEUE_NAME = "hello-world";

    //接收消息
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

        //5 消费消息
        DeliverCallback deliverCallback=(consumerTag, message)->{
            System.out.println(new String(message.getBody()));
        };
        CancelCallback cancelCallback=consumerTag-> System.out.println("消费被中断");
        //参数挨个为：队列，消费成功后是否要自动应答，成功消费的回调，未成功消费的回调
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
    }
}
