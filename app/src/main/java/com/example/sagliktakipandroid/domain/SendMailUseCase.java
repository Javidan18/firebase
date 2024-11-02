package com.example.sagliktakipandroid.domain;

import com.example.sagliktakipandroid.core_utils.MailAPI;

public class SendMailUseCase {

    public String invoke(String emailAddress) {
        String subjectOfMail = "OTP Code";
        String message = "Your OTP code is ";
        String otpCode = String.valueOf((int) (Math.random() * 9000 + 1000));

        MailAPI api = new MailAPI(emailAddress, subjectOfMail, message, otpCode);
        api.execute();

        return otpCode;
    }


}
