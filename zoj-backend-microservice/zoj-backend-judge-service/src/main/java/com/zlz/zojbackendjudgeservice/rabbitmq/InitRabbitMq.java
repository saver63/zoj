package com.zlz.zojbackendjudgeservice.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于创建测试程序用到的交换机和队列（只用在程序启动前执行一次）
 */
@Slf4j
public class InitRabbitMq {

    public static void doInit(){
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            String EXCHANGE_NAME = "code_exchange";
            //channel是rabbitMq的客户端
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            //创建小zhao队列，没有指定队列时随机分配一个队列名称
            String queueName ="code_queue";
            channel.queueDeclare(queueName, true, false, false, null);
            //绑定队列
            channel.queueBind(queueName, EXCHANGE_NAME, "my_routingKey");
            log.info("消息队列启动成功");
        }catch (Exception e){
            log.error("启动失败");
        }
    }

    public static void main(String[] args) {

        doInit();
    }
}
