package com.jiebao.platfrom.accident.daomain;

import java.lang.reflect.Method;

public enum em {
    Ql(2, 3), jk(3, 6);
    private final int size;
    private final int height;

    em(int size, int height) {
        this.size = size;
        this.height = height;
    }

    public void say() {
        System.out.println("执行第一个方法");
        System.out.println(size);
    }

    public void hel() {
        System.out.println("第二个方法");
        System.out.println(height);
    }

    public static em getEum(String name){
        return valueOf(name);
    }



    public static void main(String[] args) {
        Class<Jk> jkClass = Jk.class;
        Method[] methods = jkClass.getMethods();
        ClassLoader classLoader = jkClass.getClassLoader();
        Class<?>[] interfaces = jkClass.getInterfaces();
    }
}
