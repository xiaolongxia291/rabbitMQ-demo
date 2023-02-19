package tracy.subscribe;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import tracy.subscribe.utils.MQutils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

//消费者2
public class Consumer2 {
    public static final String QUEUE_NAME2="q2";//队列名称

    public static void main(String[] args) throws TimeoutException, IOException {
        //获取信道
        Channel channel=MQutils.getChannel();
        System.out.println("消费者2等待接收消息...");

        //消费消息
        DeliverCallback deliverCallback=(consumerTag, message)->{
            System.out.println(new String(message.getBody()));
            //消息应答
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
        };
        CancelCallback cancelCallback=consumerTag-> System.out.println("消费被中断");
        //参数挨个为：队列，消费成功后是否要自动应答，成功消费的回调，未成功消费的回调
        channel.basicConsume(QUEUE_NAME2,false,deliverCallback,cancelCallback);
    }
}
