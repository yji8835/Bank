package bank;

import common.AccountType;

abstract class Account{
    abstract String display();

}

public class SavingAccount extends Account{ //저축예금계좌
    private double InterestRate;
    private long MaxTransferAmountToChecking;
    private AccountVO account;
    public SavingAccount(double inter, long maxtransfer){
        this.InterestRate = inter;
        this.MaxTransferAmountToChecking = maxtransfer;
    }

    @Override
    String display() {
        if(InterestRate != 0) {
            return null;
        }
        return null;
    }
}
