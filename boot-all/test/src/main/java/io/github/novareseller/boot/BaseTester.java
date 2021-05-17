package io.github.novareseller.boot;

/**
 * @author: Bowen huang
 * @date: 2021/05/13
 */
public class BaseTester {

    public void sleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
