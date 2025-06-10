package com.diplom.demo.Service;

import com.diplom.demo.DTO.OrderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyWaiters(String message) {
        messagingTemplate.convertAndSend("/topic/waiter-alerts", message);
    }

    public void notifyCooks(String message) {
        messagingTemplate.convertAndSend("/topic/cooks", message);
    }

    public void notifyCooks(OrderDTO orderDTO) {
        messagingTemplate.convertAndSend("/topic/cooks", orderDTO);
    }
}