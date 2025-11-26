package com.example.moominframework;

public class BeanDefinition {

    // 빈을 생성하는 모든 스펙을 담고 있는 공간
    // 실제로는 굉장히 많은 데이터들을 가지고 있음
    private final Class<?> beanClass;

    public BeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }
}
