# 순수 Java DI 프레임워크 구현 PROJECT

## 🎯 목표
Spring Framwork와 같은 외부 의존성 없이 직접 DI 컨테이너를 설계/구현 하는 것

<br>

## 🪄 예시 도메인 구조
아래와 같이 하나의 서비스와 세 개의 레포지토리가 존재한다고 가정 <br>
모든 레포지토리는 @Bean(혹은 이와 비슷한 역할의)어노테이션이 붙어있어야 하며 <br>
UserService는 세 개의 Repository에 의존하는 형태


<br>

## ✔️ 요구사항
1️⃣ @Bean 어노테이션
- 클래스 레벨에서 사용합니다
- DIContainer가 관리할 객체임을 나타냅니다
```
@Bean
public class UserRepository { ... }
```
<br>

2️⃣ DIContainer 클래스
- 생성자
```
DIContainer container = new DIContainer("com.example.app");
```
DIContainer 생성 시 다음 작업을 수행해야 함
1. 지정된 패키지를 스캔
2. @Bean이 선언된 클래스를 BeanDefinition으로 등록
3. 실제 객체 인스턴스는 getBean 호출 시점에 생성하는 Lazy Singleton 방식으로 처리
<br>

- getBean(Class<T> type)
```
UserService service = container.getBean(UserService.class);
```
동작 규칙은 다음과 같음
1. 싱글턴 캐시에 이미 존재하면 즉시 반환
2. 존재하지 않는다면
   - public 생성자가 1개 존재한다고 가정하고 이를 조회
   - 생성자의 모든 파라미터 타입을 조회
   - 각 파라미터 타입이 Bean으로 등록되어 있는지 검사
   - Bean이 아니라면 즉시 예외를 발생
   - Bean이라면 필요한 의존성을 먼저 생성
   - 모든 의존성을 확보한 뒤, 대상 Bean을 생성하며 싱글턴 캐시에 저장
3. 생성된 인스턴스를 반환 


<br>

## 🔴 예외 규칙
아래 조건에 해당하는 경우 예외를 발생시켜야 함 <br>
1️⃣ 미등록 Bean 의존성
- 생성자 파라미터 타입이 @Bean으로 등록되어있지 않은 경우

2️⃣ 중복 Bean
- 동일 타입의 Bean이 여러 개 발견된 경우

3️⃣ 생성자 규칙 위반
- public 생성자가 없거나 2개 이상 있는 경우

## ✨ 실행
프레임워크를 완성하면 아래와 같이 사용할 수 있어야 함
```
public class AppMain {
    public static void main(String[] args) {
        // 1. DIContainer 초기화
        MoominContainer container = new MoominContainer("com.example.app");

        // 2. Bean가져오기
        UserService userService = container.getBean(UserService.class);

        // 3. 정상 동작 확인용 호출
        userService.run();
    }
}
```
