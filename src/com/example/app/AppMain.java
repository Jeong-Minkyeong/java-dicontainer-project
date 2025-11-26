package com.example.app;

import com.example.app.service.UserService;
import com.example.moominframework.MoominContainer;

public class AppMain {
    public static void main(String[] args) {
        // 1. DIContainer 초기화 (스캔할 패키지 지정)
        MoominContainer container = new MoominContainer("com.example.app");

        // 2. Bean가져오기 (필요한 의존성 자동 주입)
        UserService userService = container.getBean(UserService.class);

        // 3. 정상 동작 확인용 호출
        userService.run();
    }
}
