package com.example.moominframework;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class MoominContainer {

    // 패키지 시작점
    private final String basePackage;

    // 클래스 저장소
    private final Map<String, Class<?>> componentClasses = new HashMap<>();

    // BeanDefinition 저장소
    private final Map<String, BeanDefinition> beanDefinitionRegistry = new HashMap<>();

    // 순환 참조 방지용 임시 저장소
    private final Set<String> currentlyCreatingBeans = new HashSet<>();

    // 싱글톤 캐시
    private final Map<String, Object> singletonObjects = new HashMap<>();

    public MoominContainer(String basePackage) {
        this.basePackage = basePackage;
        scanBasePackage();
        scanForComponents();
    }

    // 패키지 스캔 메서드
    // TODO: file 이외의 다른 형식도 탐색 가능하도록 확장
    private void scanBasePackage() {
        try {
            String path = basePackage.replace('.', '/');
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = cl.getResources(path);

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                scanDirectoryEndPoint(url);
            }
        } catch (Exception e) {
            throw new RuntimeException("패키지 스캔 중 오류가 발생했습니다.", e);
        }
    }

    private void scanDirectoryEndPoint(URL url) throws URISyntaxException {
        File dir = new File(url.toURI());
        scanDirectory(basePackage, dir);
    }


    // 디렉토리 스캔 메서드
    private void scanDirectory(String currentPackage, File dir) {
        File[] files = dir.listFiles();
        if(files == null) return;

        for(File file : files) {
            // 디렉토리이면 재탐색
            if(file.isDirectory()) {
                String subPackage = currentPackage + "." + file.getName();
                scanDirectory(subPackage, file);
            }

            // .class 파일이면 클래스 객체로 변환하여 저장
            if (file.getName().endsWith(".class") && !file.getName().contains("$")) {
                String className = currentPackage + "." + file.getName().substring(0, file.getName().length() - 6);
                System.out.println("찾은 클래스 파일 이름 : " + className);

                try {
                    Class<?> clazz = Class.forName(className);
                    componentClasses.put(className, clazz);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // @MoominComponent 필터링 및 빈 등록
    private void scanForComponents() {

        System.out.println();
        System.out.println("--@MoominComponet가 붙은 클래스--");

        for(Class<?> clazz : componentClasses.values()) {
            if(clazz.isAnnotationPresent(MoominComponent.class)){

                // 이름 변경(카멜)
                String beanName = toLowerCamel(clazz.getSimpleName());

                // BeanDefinition 생성
                BeanDefinition bd = new BeanDefinition(clazz);

                // 저장
                beanDefinitionRegistry.put(beanName, bd);

                System.out.println("빈 등록 완료 : " + bd.getBeanClass().getName());
            }
        }
        System.out.println();
    }

    private String toLowerCamel(String className){
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }

    // 빈 생성 메서드
    private Object createBean(String beanName) {

        // 순환 참조 검사
        if(currentlyCreatingBeans.contains(beanName)){
            throw new RuntimeException("빈 생성 불가 - 순환 의존성 발견 " + beanName);
        }

        // 생성 대기열 추가
        currentlyCreatingBeans.add(beanName);

        try {
            // BeanDefinition 조회 및 클래스 로딩
            BeanDefinition bd = beanDefinitionRegistry.get(beanName);
            Class<?> clazz = bd.getBeanClass();

            // 생성자 선택
            Constructor<?> constructor = clazz.getDeclaredConstructors()[0];

            // 의존성 분석 밎 주입
            Class<?>[] paramType = constructor.getParameterTypes();
            Object[] args = new Object[paramType.length];

            // 재귀 호출
            for(int i = 0; i<paramType.length; i++){
                String dependencyName = toLowerCamel(paramType[i].getSimpleName());
                args[i] = getBean(dependencyName);
            }

            // 빈 생성
            Object bean = constructor.newInstance(args);
            System.out.println("빈 생성 완료 : " + bean.getClass().getSimpleName());

            // 싱글톤 캐시에 저장
            singletonObjects.put(beanName, bean);

            return bean;

        } catch (Exception e) {
            throw new RuntimeException("빈 생성 중 오류가 발생했습니다. " + e);

        } finally {
            // 생성 대기열 제거
            currentlyCreatingBeans.remove(beanName);
        }
    }

    // 빈 호출 메서드
    private Object getBean(String beanName) {
        try{
            // 싱글톤 캐시에 빈이 있다면
            if(singletonObjects.containsKey(beanName)){
                return singletonObjects.get(beanName);
            }

            // 싱글톤 캐시에 빈이 없다면
            BeanDefinition bd = beanDefinitionRegistry.get(beanName);
            Object bean = createBean(beanName);

            return bean;
        } catch (Exception e){
            throw new RuntimeException("빈 조회가 실패했습니다. " + e);
        }
    }

    // getBean 진입 메서드
    public <T> T getBean(Class<T> type) {
        String beanName = toLowerCamel(type.getSimpleName());
        return type.cast(getBean(beanName));
    }
}
