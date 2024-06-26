package com.zlz.zojbackendjudgeservice.rabbitmq;

import com.rabbitmq.client.Channel;
import com.zlz.zojbackendjudgeservice.judge.JudgeService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MyMessageConsumer {

    @Resource
    private JudgeService judgeService;

    //不想抛异常可以使用@SneakyThrows注解，不建议这样做
    @SneakyThrows
    //指定程序监听的消息队列和确认机制(MANUAL：可以自定义)
    @RabbitListener(queues = {"code_queue"},ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliverTag){
        log.info("receiveMessage = {}",message);
        long questionSubmitId = Long.parseLong(message);

        try {
            judgeService.doJudge(questionSubmitId);
            channel.basicAck(deliverTag, false);
        }catch (Exception e){
            //失败后重新处理
            channel.basicNack(deliverTag, false, false);
        }

    }
}