package tracy.workqueues.utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MQutils {
    //获取一个channel
    public static Channel getChannel() throws IOException, TimeoutException {
        //1 创建一个连接工厂
        ConnectionFactory factory=new ConnectionFactory();
        //2 设置工厂ip等信息 连接rabbitMQ
        factory.setHost("你的rabbitMQ服务器ip");
        factory.setPort(5673);//通信端口
        factory.setUsername("guest");//默认的用户名
        factory.setPassword("guest");//默认的密码
        //3 创建连接
        Connection connection=factory.newConnection();
        //4 获取信道
        return connection.createChannel();
    }
}
