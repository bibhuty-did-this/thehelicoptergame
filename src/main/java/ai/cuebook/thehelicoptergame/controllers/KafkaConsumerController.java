package ai.cuebook.thehelicoptergame.controllers;

import ai.cuebook.thehelicoptergame.entity.kafka.WarEvent;
import ai.cuebook.thehelicoptergame.manager.KafkaConsumerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("consume")
public class KafkaConsumerController {

    @Autowired
    private KafkaConsumerManager kafkaConsumerManager;

    @KafkaListener(groupId = "warEvent",topics = "warEvent",containerFactory = "kafkaListenerContainerFactory")
    public void getMsgFromTopic(WarEvent warEvent){
        kafkaConsumerManager.processMessage(warEvent);
    }
}
