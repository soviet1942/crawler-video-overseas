package com.motorfans.test;

import com.motorfans.common.Context;

public class test {
    public static void main(String[] args) {
        String a = Context.getContext().getProp(Context.TENCENT_YUNDISK_PASSWORD);
        System.out.println(a);
    }
}
