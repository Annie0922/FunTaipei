package com.example.funtaipei.member;

import java.io.Serializable;
import java.util.Date;

public class Member implements Serializable {
    private int Mb_no;
    private String Mb_email;
    private String Mb_password;
    private String Mb_name;
    private String Mb_gender;
    private Date Mb_birthday;
    private int Mb_status;

    public Member(int mb_no, String mb_email, String mb_password,  String mb_name, String mb_gender,
                  Date mb_birthday, int mb_status) {
        super();
        Mb_no = mb_no;
        Mb_email = mb_email;
        Mb_password = mb_password;
        Mb_name = mb_name;
        Mb_gender = mb_gender;
        Mb_birthday = mb_birthday;
        Mb_status = mb_status;
    }

    public int getMb_no() {
        return Mb_no;
    }

    public void setMb_no(int mb_no) {
        Mb_no = mb_no;
    }

    public String getMb_email() {
        return Mb_email;
    }

    public void setMb_email(String mb_email) {
        Mb_email = mb_email;
    }

    public String getMb_password() {
        return Mb_password;
    }

    public void setMb_password(String mb_password) {
        Mb_password = mb_password;
    }


    public String getMb_name() {
        return Mb_name;
    }

    public void setMb_name(String mb_name) {
        Mb_name = mb_name;
    }

    public String getMb_gender() {
        return Mb_gender;
    }

    public void setMb_gender(String mb_gender) {
        Mb_gender = mb_gender;
    }

    public Date getMb_birthday() {
        return Mb_birthday;
    }

    public void setMb_birthday(Date mb_birthday) {
        Mb_birthday = mb_birthday;
    }

    public int getMb_Status() {
        return Mb_status;
    }

    public void setMb_Status(int mb_status) {
        Mb_status = mb_status;
    }

    public void setFields(String mb_name, String mb_gender, Date mb_birthday) {
        this.Mb_name = mb_name;
        this.Mb_gender = mb_gender;
        this.Mb_birthday = mb_birthday;
    }
}
