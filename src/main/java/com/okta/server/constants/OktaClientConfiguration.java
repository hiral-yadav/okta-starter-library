package com.okta.server.constants;

public enum OktaClientConfiguration {
    //    issuer(""),
    //    clientId(""),
    //    clientSecret(""),
    //    audience(""),
    //    scopes(""),
    appId(""),
    ssws(""),
    domain(""),
    users(""),
    add(""),
    assign(""),
    admin("");

    private final String value;

    OktaClientConfiguration(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
