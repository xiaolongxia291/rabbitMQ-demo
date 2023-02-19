package tracy.workqueues;

import com.rabbitmq.client.*;
import tracy.workqueues.utils.MQutils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

//消费者 工作线程
public class Consumer implements Runnable{
    //public static final String QUEUE_NAME="queue3.0";//同步发布确认的队名
    public static final String QUEUE_NAME="queue4.0";//异步发布确认的队名

    @Override
    public void run() {
        try {
            Channel channel = MQutils.getChannel();
            //设置分发策略为不公平分发
            channel.basicQos(1);

            DeliverCallback deliverCallback=(consumerTag, message)->{
                System.out.println(Thread.currentThread().getName()+":"+new String(message.getBody()));
                //确认应答，只确认当前消息
                channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
            };
            //关闭自动确认应答
            channel.basicConsume(QUEUE_NAME,false,deliverCallback,consumerTag-> {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //接收消息
    public static void main(String[] args) throws TimeoutException, IOException {
        //启动两个消费者/工作线程
        Runnable runnable= new Consumer();
        new Thread(runnable).start();
        new Thread(runnable).start();
    }
}
