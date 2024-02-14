package com.example.foobar_dt_ad;

import androidx.annotation.Nullable;

import java.util.Objects;

public class Member {
    private final String Email;
    private final String firstName;
    private final String lastName;
    private final String password;
    public Member(String Email,String firstName, String lastName, String password) {
        this.Email = Email;
        this.firstName = lastName;
        this.lastName = lastName;
        this.password = password;
    }

    public String getEmail() {
        return Email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public boolean equals(Member member) {
        return this.Email.equals(member.getEmail());
    }
}
