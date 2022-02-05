package com.example.demo.domain;

import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class Book {
    private Long memberId;
    private String bookName;
    private int price;

    public Book(String bookName, int price) {
        this.bookName = bookName;
        this.price = price;
    }

    public Book() {
    }

//    public Book(Long a, String b, int c, Long d, String e, int f) {
//
//    }
    //No constructor found in com.example.demo.domain.Book matching [java.lang.Long, java.lang.String, java.lang.Integer, java.lang.Long, java.lang.String, java.lang.Intege
    //가짜 생성자..

}
