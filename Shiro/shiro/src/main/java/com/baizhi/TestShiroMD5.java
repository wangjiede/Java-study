package com.baizhi;

import org.apache.shiro.crypto.hash.Md5Hash;

public class TestShiroMD5 {
    public static void main(String[] args) {

        //创建一个md5算法
//        Md5Hash md5Hash = new Md5Hash();
//        md5Hash.setBytes("123".getBytes());
//        String s = md5Hash.toHex();
//        System.out.println(s);

        //使用md5
        Md5Hash md5Hash = new Md5Hash("123");

        System.out.println(md5Hash.toHex());

        //使用MD5 + salt处理
        Md5Hash md5Hash1 = new Md5Hash("123", "X0*7ps");

        System.out.println(md5Hash1.toHex());

        //使用md5 + salt + hash散列
        Md5Hash md5Hash2 = new Md5Hash("123", "X0*7ps", 1024);
        System.out.println(md5Hash2.toHex());


    }
}
