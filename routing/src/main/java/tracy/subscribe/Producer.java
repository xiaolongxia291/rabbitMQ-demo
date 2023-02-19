package tracy.subscribe;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.MessageProperties;
import tracy.subscribe.utils.MQutils;

import java.io.IOException;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

//生产者
public class Producer {
    public static final String EXCHANGE_NAME="direct_exchange";//交换机名称
    public static final String QUEUE_NAME1="q1";//队列名称
    public static final String QUEUE_NAME2="q2";//队列名称
    public static final String RKEY1_1="orange";//与q1绑定的routingKey
    public static final String RKEY2_1="black";//与q2绑定的routingKey
    public static final String RKEY2_2="green";//与q2绑定的routingKey


    public static void main(String[] args) throws TimeoutException, IOException, InterruptedException {
        //获取信道
        Channel channel=MQutils.getChannel();

        //开启发布确认
        channel.confirmSelect();

        //声明一个exchange，参数为名称和类型
        channel.exchangeDeclare(EXCHANGE_NAME,"direct");

        //声明两个队列
        channel.queueDeclare(QUEUE_NAME1,true,false,false,null);
        channel.queueDeclare(QUEUE_NAME2,true,false,false,null);

        //绑定交换机与队列，参数为队列名、交换机名，routingKey
        channel.queueBind(QUEUE_NAME1,EXCHANGE_NAME,RKEY1_1);
        channel.queueBind(QUEUE_NAME2,EXCHANGE_NAME,RKEY2_1);
        channel.queueBind(QUEUE_NAME2,EXCHANGE_NAME,RKEY2_2);

        //异步发布确认
        ConcurrentSkipListMap<Long,String> failedMessage=new ConcurrentSkipListMap<>();
        ConfirmCallback ackCallback=(deliveryTag, multiple)->{
            if(multiple){
                failedMessage.headMap(deliveryTag).clear();
            }else{
                failedMessage.remove(deliveryTag);
            }
        };
        ConfirmCallback nackCallback=(deliveryTag,multiple)->{
            System.out.println("未确认的消息："+deliveryTag);
        };
        channel.addConfirmListener(ackCallback,nackCallback);

        //发布消息
        for(int i=0;i<20;++i) {
            String message="message"+i;
            //将所有消息存储在failedMap中
            failedMessage.put(channel.getNextPublishSeqNo(),message);
            //发布消息时开启消息 持久化
            if((i+1)%3==0)channel.basicPublish(EXCHANGE_NAME,RKEY1_1, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
            else if((i+1)%3==1)channel.basicPublish(EXCHANGE_NAME,RKEY2_1, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
            else channel.basicPublish(EXCHANGE_NAME,RKEY2_2, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
        }
        System.out.println("生产者发送完毕");
    }
}
