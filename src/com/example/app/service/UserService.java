package com.example.app.service;

import com.example.app.repository.NotificationRepository;
import com.example.app.repository.OrderRepository;
import com.example.app.repository.UserRepository;
import com.example.moominframework.MoominComponent;

@MoominComponent
public class UserService {

    public UserService (
            UserRepository userRepository,
            OrderRepository orderRepository,
            NotificationRepository notificationRepository
    ) {
        // 의존성 주입 필요
    }

    public void run() {
        System.out.println("=== Moomin Container 가 정상적으로 실행되었습니다 ===");
    }
}
