package bank;

import common.AccountType;

abstract class Account{
    abstract public String display(AccountVO accountVO);

}

public class SavingsAccount extends AccountVO{ //저축예금계좌
    private double InterestRate;
    private long MaxTransferAmountToChecking;
    public AccountVO account;

    public SavingsAccount(){
    }
    public SavingsAccount(AccountVO account,double inter, long maxtransfer){
        this.account = account;
        this.InterestRate = inter;
        this.MaxTransferAmountToChecking = maxtransfer;
    }
    public AccountVO getAccount() {
        return account;
    }
    public double getInterestRater() {
        return InterestRate;
    }


    public long getMaxTransferAmountToChecking() {
        return MaxTransferAmountToChecking;
    }
    public void setMaxTransferAmountToChecking(long linkedSavings) {
        this.MaxTransferAmountToChecking = MaxTransferAmountToChecking;
    }

    @Override
    public String toString() {
        return "SavingsAccount{" +
                "account='" + account + '\'' +
                ", InterestRate=" + InterestRate +
                ", MaxTransferAmountToChecking=" + MaxTransferAmountToChecking +
                '}';
    }

    public void display() {
        System.out.println(toString());
    }
}
