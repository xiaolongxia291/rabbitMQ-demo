package tracy.workqueues;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.MessageProperties;
import tracy.workqueues.utils.MQutils;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

//生产者   基于异步发布
public class AsynProducer {
    //队列名称
    public static final String QUEUE_NAME="queue4.0";

    //生产消息
    public static void main(String[] args) throws TimeoutException, IOException, InterruptedException {
        Channel channel=MQutils.getChannel();
        //开启发布确认
        channel.confirmSelect();

        //声明队列时，开启队列持久化
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        //声明一个线程安全的哈希表，用来存放发送失败的消息
        ConcurrentSkipListMap<Long,String> failedMessage=new ConcurrentSkipListMap<>();
        //消息监听器，哪些成功了，哪些失败了
        ConfirmCallback ackCallback=(deliveryTag,multiple)->{
            //将发布成功的从failedMap中删除，剩下的就是发布未成功的
            if(multiple){//批量
                failedMessage.headMap(deliveryTag).clear();
            }else{//单个
                failedMessage.remove(deliveryTag);
            }
        };
        ConfirmCallback nackCallback=(deliveryTag,multiple)->{
            System.out.println("未确认的消息："+deliveryTag);
        };
        //第一个参数为发布成功的回调，第二个为发布失败的回调，需要手动实现
        channel.addConfirmListener(ackCallback,nackCallback);

        //记录发布前后耗时
        long start=System.currentTimeMillis();
        for(int i=0;i<1000;++i){
            String message="message"+i;
            //将所有消息存储在failedMap中
            failedMessage.put(channel.getNextPublishSeqNo(),message);
            //发布消息时开启消息持久化
            channel.basicPublish("",QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
        }
        long end=System.currentTimeMillis();
        System.out.println("生产者发送完成，耗时："+(end-start)/1000.0d+"s");
    }
}
