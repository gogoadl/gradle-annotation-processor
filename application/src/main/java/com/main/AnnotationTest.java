package com.main;

import com.annotation.PerformanceTest;

@PerformanceTest
public class AnnotationTest {
    public String s;
    public int a;
    public void run() {

        for (int i = 0; i < 1000000000; i++) {
            a+=i;
        }
    }
}
