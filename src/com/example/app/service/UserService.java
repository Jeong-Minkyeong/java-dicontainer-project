package com.example.app.service;

import com.example.app.repository.NotificationRepository;
import com.example.app.repository.OrderRepository;
import com.example.app.repository.UserRepository;
import com.example.moominframework.MoominComponent;

@MoominComponent
public class UserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final NotificationRepository notificationRepository;

    public UserService (
            UserRepository userRepository,
            OrderRepository orderRepository,
            NotificationRepository notificationRepository
    ) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.notificationRepository = notificationRepository;
    }

    public void run() {
        System.out.println("=== Moomin Container 가 정상적으로 실행되었습니다 ===");
    }
}
