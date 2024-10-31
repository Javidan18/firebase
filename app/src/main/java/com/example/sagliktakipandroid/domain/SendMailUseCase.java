package com.example.sagliktakipandroid.domain;

import com.example.sagliktakipandroid.core_utils.MailAPI;

public class SendMailUseCase {

    public void invoke(String emailAddress, OtpCallback callback) {
        String subjectOfMail = "OTP Code";
        String message = "Your OTP code is ";
        String otpCode = String.valueOf((int) (Math.random() * 9000 + 1000));

        MailAPI api = new MailAPI(emailAddress, subjectOfMail, message, otpCode);
        api.execute();

        callback.onOtpGenerated(otpCode);
    }

    public interface OtpCallback {
        void onOtpGenerated(String otpCode);
    }


}
