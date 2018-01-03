package com.blackcrowsys.raven.model;

public class Converter {

    public Money stringToMoney(String value) {
        Money money = new Money();
        money.setCurrency("GBP");
        money.setValue(1000L);
        return money;
    }

    public String moneyToString(Money money) {
        return "USD 2000";
    }
}
