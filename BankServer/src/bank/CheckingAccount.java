package bank;

import common.AccountType;

import java.sql.Date;

//totalbalance, availablebalance

public class CheckingAccount extends AccountVO {

    private SavingsAccount linkedSavings;
    public CheckingAccount(String owner, String accountNo, long balance, Date openDate) {
        super(owner, accountNo, AccountType.CHECKING, balance, openDate);
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
                "owner='" + getOwner() + '\'' +
                ", accountNo='" + getAccountNo() + '\'' +
                ", type=" + getType() +
                ", balance=" + getBalance() +
                ", openDate=" + getOpenDate() +
                ", linkedSavings=" + (linkedSavings != null ? linkedSavings : "None") +
                '}';
    }

    public void display() {
        System.out.println(toString());
    }

}

