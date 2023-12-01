package bank;

import common.AccountType;

abstract class Account{
    abstract public String display(AccountVO accountVO);

}

public class SavingsAccount extends Account{ //저축예금계좌
    private double InterestRate;
    private long MaxTransferAmountToChecking;
    private AccountVO account;

    public SavingsAccount(){
    }
    public SavingsAccount(AccountVO account,double inter, long maxtransfer){
        this.account = account;
        this.InterestRate = inter;
        this.MaxTransferAmountToChecking = maxtransfer;
    }
    @Override
    public String display(AccountVO account) {
        if(this.InterestRate != 0){
            return account.getOwner() + " " + InterestRate +" " + account.getType();
        }
        return null;
    }
}
