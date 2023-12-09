package bank;

import common.AccountType;

import java.sql.Date;

//totalbalance, availablebalance

public class CheckingAccount extends AccountVO {

    private SavingsAccount linkedSavings;
    AccountVO account;

    public  CheckingAccount(AccountVO account, SavingsAccount linkedSavings) {
        this.account = account;
        this.linkedSavings = linkedSavings;
    }
    public AccountVO getAccount() {
        return account;
    }
    public SavingsAccount getLinkedSavings() {
        return linkedSavings;
    }
    public void setLinkedSavings(SavingsAccount linkedSavings) {
        this.linkedSavings = linkedSavings;
    }

    @Override
    public String toString() {
        return "CheckingAccount{" +
                "account" + account +
                ", linkedSavings=" + (linkedSavings != null ? linkedSavings : "None") +
                '}';
    }

    public void display() {
        System.out.println(toString());
    }

}

