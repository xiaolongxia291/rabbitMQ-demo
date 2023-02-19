package tracy.workqueues;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import tracy.workqueues.utils.MQutils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

//生产者
public class Producer {
    public static final String QUEUE_NAME="queue3.0";

    //生产消息
    public static void main(String[] args) throws TimeoutException, IOException, InterruptedException {
        Channel channel=MQutils.getChannel();
        //分发策略

        //开启发布确认
        channel.confirmSelect();

        //声明队列时，开启队列持久化
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);

        //记录发布前后耗时
        long start=System.currentTimeMillis();

        int batch_size=100;//每100条确认一次
        for(int i=0;i<1000;++i){
            String message="message"+i;
            //发布消息时开启消息 持久化
            channel.basicPublish("",QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
            //每100条等待确认一次
            if((i+1)%batch_size==0)channel.waitForConfirms();
        }
        long end=System.currentTimeMillis();
        System.out.println("生产者发送完成，耗时："+(end-start)/1000.0d+"s");
    }
}
