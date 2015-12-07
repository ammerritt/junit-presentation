package com.merritt.samples.testing.encryption.action;

import org.apache.commons.lang3.StringUtils;

public class PasswordEncrypter {
    public String encrypt(final String password) {
        return StringUtils.reverse(password);
    }

    public String decrypt(final String encPassword) {
        return StringUtils.reverse(encPassword);
    }
}
