package bank;

abstract class Account{
    abstract String display();

}

public class SavingAccount extends Account{ //���࿹�ݰ���
    private double InterestRate;
    private long MaxTransferAmountToChecking;
    private AccountVO account;
    @Override
    String display() {
        return null;
    }
}
