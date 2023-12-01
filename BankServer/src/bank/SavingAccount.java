package bank;

abstract class Account{
    abstract String display();

}

public class SavingAccount extends Account{ //저축예금계좌
    private double InterestRate;
    private long MaxTransferAmountToChecking;
    private AccountVO account;
    @Override
    String display() {
        return null;
    }
}
