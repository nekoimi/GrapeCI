package com.sakuraio.jenkins.ci.utils

import java.security.MessageDigest

/**
 * <p>CodecUtils</p>
 *
 * @author nekoimi 2022/11/16
 */
class CodecUtils {

    private CodecUtils() {}

    static String sha1(String text) {
        MessageDigest sha1 = MessageDigest.getInstance("SHA1")
        byte[] digest  = sha1.digest(text.getBytes())
        return new BigInteger(1, digest).toString(16)
    }
}
