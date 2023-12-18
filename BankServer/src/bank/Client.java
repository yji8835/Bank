package bank;
import common.AccountType;
import common.CommandDTO;
import common.ResponseType;
import common.AccountType;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


//*******************************************************************
// # 52
//*******************************************************************
// Name : Client
// Type : Class
// Description :  BankServer�� ���� ��û�� ATM ���ϰ��� ����� ����ϴ� ���� Class ����.
//                �޽��� Receive, Send �� ����, ���, ���� ��ü ���� ����� ���� �Ǿ� �ִ�.
//*******************************************************************
public class Client {
    private AsynchronousSocketChannel clientChannel;
    private ClientHandler handler;
    private List<CustomerVO> customerList;


    public Client(AsynchronousSocketChannel clientChannel, ClientHandler handler, List<CustomerVO> customerList) {
        this.clientChannel = clientChannel;
        this.handler = handler;
        this.customerList = customerList;
        receive();
    }

    //*******************************************************************
    // # 52-01
    //*******************************************************************
    // Name : receive()
    // Type : method
    // Description :  ������ �޽����� ���۸� CommandDTO Class ���·� ����ȯ ��
    //                ��û ������ �Ľ��Ͽ� �� ��û�� ���� ����� ����
    //*******************************************************************
    private void receive() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        clientChannel.read(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (result == -1) {
                    disconnectClient();
                    return;
                }
                attachment.flip();
                try {
                    // ���� ������ �Ľ�
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(attachment.array());
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    CommandDTO command = (CommandDTO) objectInputStream.readObject();
                    switch (command.getRequestType()) {
                        case VIEW:
                            view(command);
                            break;
                        case LOGIN:
                            login(command);
                            break;
                        case TRANSFER:
                            transfer(command);
                            break;
                        case DEPOSIT:
                            deposit(command);
                            break;
                        case WITHDRAW:
                            withdraw(command);
                            break;
                        case NEWACCOUNT:
                            newaccount(command);
                            break;
                        case DELETEACCOUNT:
                            delaccount(command);
                            break;
                        case VIEWACCOUNT:
                            viewaccount(command);
                            break;
                        default:
                            break;
                    }
                    // ������ ���� �� �ٽ� �б� ����
                    ByteBuffer byteBuffer = ByteBuffer.allocate(512);
                    clientChannel.read(byteBuffer, byteBuffer, this);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                disconnectClient();
            }
        });
    }

    //*******************************************************************
    // # 52-02
    //*******************************************************************
    // Name : send()
    // Type : method
    // Description :  ����� ATM ���Ͽ� CommandDTO ���·� �޽����� ����
    //                CommandDTO ���ο� ��û������ ��� ������ ���� �ؾ� �Ѵ�
    //*******************************************************************
    private void send(CommandDTO commandDTO) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(commandDTO);
            objectOutputStream.flush();
            clientChannel.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //*******************************************************************
    // # 52-03
    //*******************************************************************
    // Name : disconnectClient()
    // Type : method
    // Description :  �ش� ������ ���ܹ߻� Ȥ�� ��� ���н� ���� ���� ��� ����
    //*******************************************************************
    private void disconnectClient() {
        try {
            clientChannel.close();
            handler.removeClient(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void viewaccount(CommandDTO commandDTO) {

        System.out.println(commandDTO.getId());
        CustomerVO customer = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getId())).findFirst().get();

        String accountno = commandDTO.getPassword();
        ArrayList<AccountVO> accountlist = customer.getAccountlist();
        for (AccountVO account : accountlist) {
            if (account instanceof CheckingAccount) {
                CheckingAccount checkingAccount = (CheckingAccount)account;
                if (accountno.equals(checkingAccount.getAccount().getAccountNo())) {
                    commandDTO.setType(AccountType.CHECKING);
                    commandDTO.setBalance(checkingAccount.getAccount().getBalance());
                    commandDTO.setInterestrate(0);
                    commandDTO.setMax(0);
                    commandDTO.setLinkedsavinge(checkingAccount.getLinkedSavings().getAccount().getAccountNo());
                    handler.displayInfo("���� " + customer.getName() + " ���� �Դϴ�." + checkingAccount);
                    send(commandDTO);

                }
            }
            if (account instanceof SavingsAccount) {
                SavingsAccount savingAccount = (SavingsAccount) account;
                if (accountno.equals(savingAccount.getAccount().getAccountNo())) {
                    commandDTO.setType(AccountType.SAVINGS);
                    commandDTO.setBalance(savingAccount.getAccount().getBalance());
                    commandDTO.setInterestrate(savingAccount.getInterestRater());
                    commandDTO.setMax(savingAccount.getMaxTransferAmountToChecking());
                    commandDTO.setLinkedsavinge("none");

                    handler.displayInfo("���� " + customer.getName() + " ���� �Դϴ�." + savingAccount);
                    send(commandDTO);
                }
            }

        }




    }


    private synchronized void delaccount(CommandDTO commandDTO) {
        CustomerVO customer = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getId())).findFirst().get();
        ArrayList<AccountVO> accountlist = customer.getAccountlist();
        if(!commandDTO.getPassword().equals(customer.getPassword())) {
            commandDTO.setResponseType(ResponseType.WRONG_PASSWORD);
            send(commandDTO);
            return;
        }

        for (AccountVO account : accountlist) {
            System.out.println(account);
            if (account instanceof CheckingAccount) {
                CheckingAccount checkingAccount = (CheckingAccount)account;
                if (commandDTO.getUserAccountNo().equals(checkingAccount.getAccount().getAccountNo())) {
                    accountlist.remove(checkingAccount);
                    commandDTO.setResponseType(ResponseType.SUCCESS);
                    handler.displayInfo(commandDTO.getUserAccountNo()+ " success delete account");
                    System.out.println(accountlist);
                    System.out.println(commandDTO);
                    send(commandDTO);
                    break;
                }
            }else if (account instanceof SavingsAccount) {
                SavingsAccount savingAccount = (SavingsAccount) account;
                if (commandDTO.getUserAccountNo().equals(savingAccount.getAccount().getAccountNo())) {
                    accountlist.remove(savingAccount);
                    commandDTO.setResponseType(ResponseType.SUCCESS);
                    handler.displayInfo(commandDTO.getUserAccountNo()+ " success delete account");
                    System.out.println(accountlist);
                    System.out.println(commandDTO);
                    send(commandDTO);
                    break;
                }
            }else {
                if (commandDTO.getUserAccountNo().equals(account.getAccountNo())) {
                    accountlist.remove(account);
                    commandDTO.setResponseType(ResponseType.SUCCESS);
                    handler.displayInfo(commandDTO.getUserAccountNo() + " success delete account");
                    System.out.println(accountlist);
                    System.out.println(commandDTO);
                    send(commandDTO);
                    break;
                }
            }

        }
        System.out.println(accountlist);
        commandDTO.setResponseType(ResponseType.FAILURE);
        handler.displayInfo(commandDTO.getUserAccountNo()+ " failure delete account");
        send(commandDTO);

    }

    private synchronized void newaccount(CommandDTO commandDTO) {

        long balance = 0;

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Date openDate = java.sql.Date.valueOf(today);

        CustomerVO customer = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getId())).findFirst().get();
        String name = customer.getName();
        ArrayList<AccountVO> accountlist = customer.getAccountlist();

        AccountVO newaccount = new AccountVO(name, commandDTO.getUserAccountNo(), commandDTO.getType(), balance, openDate);
        if (commandDTO.getType() == common.AccountType.CHECKING) {

            for (AccountVO account : accountlist) {
                if (account instanceof SavingsAccount) {
                    SavingsAccount savingAccount = (SavingsAccount) account;
                    if (savingAccount.getAccount().getAccountNo().equals(commandDTO.getLinkedsaving())) {
                        newaccount = new CheckingAccount(newaccount, savingAccount);
                    }
                }
            }

        } else if (commandDTO.getType() == common.AccountType.SAVINGS) {
            newaccount = (new SavingsAccount(newaccount, commandDTO.getInterestrate(), commandDTO.getMax()));
        }


        customer.addaccount(newaccount);
        commandDTO.setResponseType(ResponseType.SUCCESS);
        handler.displayInfo(commandDTO.getUserAccountNo()+ " success new account");

        try {
            CommandDTO common = new CommandDTO();
            common.setResponseType(ResponseType.SUCCESS);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(common);
            objectOutputStream.flush();
            clientChannel.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    //*******************************************************************
    // # 52-01-01
    //*******************************************************************
    // Name : login()
    // Type : method
    // Description : ATM�� Loging ��û ��� ����
    //               ����, ���� ���θ� ����޽����� ����
    //*******************************************************************
    private synchronized void login(CommandDTO commandDTO) {
        Optional<CustomerVO> customer = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getId()) && Objects.equals(customerVO.getPassword(), commandDTO.getPassword())).findFirst();

        if (customer.isPresent()) {
            commandDTO.setResponseType(ResponseType.SUCCESS);
            String text = customer.get().getName();
            String text2 = "���� �α����Ͽ����ϴ�.";
            String text3 = text + text2;
            handler.displayInfo(customer.get().getName() + "���� �α����Ͽ����ϴ�.");
        } else {
            commandDTO.setResponseType(ResponseType.FAILURE);
        }
        send(commandDTO);
    }

    //*******************************************************************
    // # 52-01-02
    //*******************************************************************
    // Name : view()
    // Type : method
    // Description : ATM�� ���� ��ȸ ��û ��� ����
    //               ���� �ܾ��� ����޽����� ����
    //*******************************************************************
    private synchronized void view(CommandDTO commandDTO) {

        CustomerVO customer = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getId())).findFirst().get();
        String account = commandDTO.getUserAccountNo();
        ArrayList<AccountVO> accountlist = customer.getAccountlist();


        AccountVO desiredAccount = null;
        for (AccountVO accountVO : accountlist) {

            if (accountVO instanceof CheckingAccount) {
                CheckingAccount checkingAccount = (CheckingAccount)accountVO;
                if (checkingAccount.getAccount().getAccountNo().equals(account)) {
                    desiredAccount = accountVO;
                    break;
                }
            }
            if (accountVO instanceof SavingsAccount) {
                SavingsAccount savingAccount = (SavingsAccount) accountVO;
                if (savingAccount.getAccount().getAccountNo().equals(account)) {
                    desiredAccount = accountVO;
                    break;
                }
            }

        }

        if (desiredAccount != null) {
            System.out.println(desiredAccount);
            commandDTO.setBalance(desiredAccount.getBalance());
            commandDTO.setUserAccountNo(desiredAccount.getAccountNo());


            handler.displayInfo(customer.getName() + "���� ���� �ܾ��� " + desiredAccount.getBalance() + "�� �Դϴ�.");
            send(commandDTO);
        } else {
        }

    }

    //*******************************************************************
    // # 52-01-03
    //*******************************************************************
    // Name : transfer()
    // Type : method
    // Description : ATM�� ���� ��ü��� ����
    //               ����, ���� ���θ� ����޽����� ����
    //*******************************************************************
    private synchronized void transfer(CommandDTO commandDTO) {

        //���� ����
        CustomerVO customer = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getId())).findFirst().get();
        String account = commandDTO.getUserAccountNo();
        ArrayList<AccountVO> accountlist = customer.getAccountlist();

        //��� ����
        AccountVO receiveaccount = null;
        System.out.println(commandDTO.getReceivedAccountNo());

        for(int i=0; i<customerList.size(); i++) {
            ArrayList<AccountVO> list = customerList.get(i).getAccountlist();
            for (AccountVO accountVO : list) {
                if (accountVO instanceof CheckingAccount) {
                    CheckingAccount checkingAccount = (CheckingAccount)accountVO;
                    if (checkingAccount.getAccount().getAccountNo().equals(commandDTO.getReceivedAccountNo())) {
                        receiveaccount = accountVO;
                        System.out.println("1");
                        break;
                    }
                }
                if (accountVO instanceof SavingsAccount) {
                    SavingsAccount savingAccount = (SavingsAccount) accountVO;
                    if (savingAccount.getAccount().getAccountNo().equals(commandDTO.getReceivedAccountNo())) {
                        receiveaccount = accountVO;
                        System.out.println("2");
                        break;
                    }
                }
            }
        }
        System.out.println("find receiver : "+receiveaccount);


        if (receiveaccount == null) { //��� ���� ����x
            System.out.println("1");
            commandDTO.setResponseType(ResponseType.WRONG_ACCOUNT_NO);
        } else if (commandDTO.getReceivedAccountNo().equals(commandDTO.getUserAccountNo())) {
            System.out.println("2");
            commandDTO.setResponseType(ResponseType.WRONG_ACCOUNT_NO);
        } else if (!customer.getPassword().equals(commandDTO.getPassword())) {
            System.out.println("3");
            commandDTO.setResponseType(ResponseType.WRONG_PASSWORD);
        }


        AccountVO desiredAccount = null;
        for (AccountVO accountVO : accountlist) {

            if (accountVO instanceof CheckingAccount) {
                System.out.println("checking");
                CheckingAccount checkingAccount = (CheckingAccount)accountVO;
                if (checkingAccount.getAccount().getAccountNo().equals(account)) {
                    desiredAccount = accountVO;


                    if (desiredAccount.getBalance() >= commandDTO.getAmount()) { //���� ������ ��� ������ ���
                        desiredAccount.setBalance(desiredAccount.getBalance() - commandDTO.getAmount());
                        receiveaccount.setBalance(receiveaccount.getBalance() + commandDTO.getAmount());
                        commandDTO.setResponseType(ResponseType.SUCCESS);
                        handler.displayInfo(commandDTO.getUserAccountNo() + " ���¿��� " + commandDTO.getReceivedAccountNo() + "���·� " + commandDTO.getAmount() + "�� ��ü�Ͽ����ϴ�.");
                    } else { // ����� �Ұ����� ���

                        //������ �ݾ� ���
                        long insufficientAmount = commandDTO.getAmount() - desiredAccount.getBalance();
                        //Savings ���� �ִ� ��ü �ݾ� �� �ܾ� Ȯ��
                        long maxTransferAmount = checkingAccount.getLinkedSavings().getMaxTransferAmountToChecking();
                        long savingbalance = checkingAccount.getLinkedSavings().getBalance();

                        boolean transfer_ok = (insufficientAmount<=maxTransferAmount) && (savingbalance>=insufficientAmount);

                        if (transfer_ok) { //Savings���� �ڵ� ��ü ����
                            desiredAccount.setBalance(0);
                            checkingAccount.getLinkedSavings().setBalance(checkingAccount.getLinkedSavings().getBalance() - insufficientAmount);
                            receiveaccount.setBalance(receiveaccount.getBalance() + commandDTO.getAmount());
                            commandDTO.setResponseType(ResponseType.SUCCESS);
                            handler.displayInfo(commandDTO.getUserAccountNo() + " ���¿��� " + commandDTO.getReceivedAccountNo() + "���·� " + (commandDTO.getAmount() -insufficientAmount) + "�� ��ü�Ͽ����ϴ�. ����� ���� ���¿��� " + insufficientAmount +"���� ��ü�Ͽ� �����߽��ϴ�.");

                        } else {
                            commandDTO.setResponseType(ResponseType.INSUFFICIENT);
                        }
                    }
                    break;
                }
            }
            if (accountVO instanceof SavingsAccount) {
                System.out.println("saving");
                SavingsAccount savingAccount = (SavingsAccount) accountVO;
                if (savingAccount.getAccount().getAccountNo().equals(account)) {
                    desiredAccount = accountVO;
                    System.out.println("sender : "+desiredAccount);

                    if (desiredAccount.getBalance() < commandDTO.getAmount()) { // ���� �ܾ� < ��� �ݾ�
                        commandDTO.setResponseType(ResponseType.INSUFFICIENT); //��� �Ұ�
                    } else { // ���� �ܾ� < ��� �ݾ� -> ��� ����
                        commandDTO.setResponseType(ResponseType.SUCCESS);
                        desiredAccount.setBalance(desiredAccount.getBalance() - commandDTO.getAmount());
                        receiveaccount.setBalance(receiveaccount.getBalance() + commandDTO.getAmount());
                        handler.displayInfo(commandDTO.getUserAccountNo() + " ���¿��� " + commandDTO.getReceivedAccountNo() + "���·� " + commandDTO.getAmount() + "�� ��ü�Ͽ����ϴ�.");
                    }
                    break;
                }
            }
        }
        System.out.println(desiredAccount);
        send(commandDTO);
    }

    //*******************************************************************
    // # 52-01-04
    //*******************************************************************
    // Name : deposit()
    // Type : method
    // Description : ATM�� ���� �Ա� ��� ����
    //               ����, ���� ���θ� ����޽����� ����
    //*******************************************************************
    private synchronized void deposit(CommandDTO commandDTO) {


        CustomerVO customer = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getId())).findFirst().get();
        String account = commandDTO.getUserAccountNo();
        ArrayList<AccountVO> accountlist = customer.getAccountlist();

        AccountVO desiredAccount = null;
        for (AccountVO accountVO : accountlist) {

            if (accountVO instanceof CheckingAccount) {
                CheckingAccount checkingAccount = (CheckingAccount)accountVO;
                if (checkingAccount.getAccount().getAccountNo().equals(account)) {
                    desiredAccount = accountVO;
                    break;
                }
            }
            if (accountVO instanceof SavingsAccount) {
                SavingsAccount savingAccount = (SavingsAccount) accountVO;
                if (savingAccount.getAccount().getAccountNo().equals(account)) {
                    desiredAccount = accountVO;
                    break;
                }
            }
        }

        if (desiredAccount != null) {
            commandDTO.setBalance(desiredAccount.getBalance());
            commandDTO.setUserAccountNo(desiredAccount.getAccountNo());

            desiredAccount.setBalance(desiredAccount.getBalance() + commandDTO.getAmount());
            commandDTO.setResponseType(ResponseType.SUCCESS);


            handler.displayInfo(customer.getName() + "���� " + desiredAccount.getAccountNo() + " ���¿� " + commandDTO.getAmount() + "�� �Ա��Ͽ����ϴ�.");
            send(commandDTO);

            System.out.println(desiredAccount);
        } else {

        }


    }

    //*******************************************************************
    // # 52-01-05
    //*******************************************************************
    // Name : deposit()
    // Type : method
    // Description : ATM�� ���� ��� ��� ����
    //               ����, ���� ���θ� ����޽����� ����
    //*******************************************************************
    private synchronized void withdraw(CommandDTO commandDTO) {

        CustomerVO customer = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getId())).findFirst().get();
        String account = commandDTO.getUserAccountNo();
        ArrayList<AccountVO> accountlist = customer.getAccountlist();


        AccountVO desiredAccount = null;

        for (AccountVO accountVO : accountlist) {
            System.out.println(accountVO);

            if (accountVO instanceof CheckingAccount) {
                CheckingAccount checkingAccount = (CheckingAccount)accountVO;
                if (checkingAccount.getAccount().getAccountNo().equals(account)) {
                    desiredAccount = accountVO;


                    if (desiredAccount.getBalance() >= commandDTO.getAmount()) { //���� ������ ��� ������ ���
                        desiredAccount.setBalance(desiredAccount.getBalance() - commandDTO.getAmount());
                        commandDTO.setResponseType(ResponseType.SUCCESS);
                        handler.displayInfo(customer.getName() + "���� " + desiredAccount.getAccountNo() + " ���¿��� " + commandDTO.getAmount() + "�� ����Ͽ����ϴ�.");
                    } else { // ����� �Ұ����� ���

                        //������ �ݾ� ���
                        long insufficientAmount = commandDTO.getAmount() - desiredAccount.getBalance();
                        //Savings ���� �ִ� ��ü �ݾ� �� �ܾ� Ȯ��
                        long maxTransferAmount = checkingAccount.getLinkedSavings().getMaxTransferAmountToChecking();
                        long savingbalance = checkingAccount.getLinkedSavings().getBalance();

                        boolean transfer_ok = (insufficientAmount<=maxTransferAmount) && (savingbalance>=insufficientAmount);

                        if (transfer_ok) { //Savings���� �ڵ� ��ü ����
                            desiredAccount.setBalance(0);
                            checkingAccount.getLinkedSavings().setBalance(checkingAccount.getLinkedSavings().getBalance() - insufficientAmount);

                            commandDTO.setResponseType(ResponseType.SUCCESS);
                            handler.displayInfo(customer.getName() + "���� " + desiredAccount.getAccountNo() + " ���¿��� " + (commandDTO.getAmount() -insufficientAmount) + "�� ����Ͽ����ϴ�. ����� ���� ���¿��� " + insufficientAmount + "���� ��ü�Ͽ� �����߽��ϴ�.");
                        } else {
                            commandDTO.setResponseType(ResponseType.INSUFFICIENT);
                        }
                    }


                    break;
                }






            }
            if (accountVO instanceof SavingsAccount) {
                SavingsAccount savingAccount = (SavingsAccount) accountVO;
                System.out.println(savingAccount);
                if (savingAccount.getAccount().getAccountNo().equals(account)) {
                    desiredAccount = accountVO;
                    System.out.println(desiredAccount);

                    if (desiredAccount.getBalance() < commandDTO.getAmount()) { // ���� �ܾ� < ��� �ݾ�
                        commandDTO.setResponseType(ResponseType.INSUFFICIENT); //��� �Ұ�
                    } else { // ���� �ܾ� < ��� �ݾ� -> ��� ����
                        commandDTO.setResponseType(ResponseType.SUCCESS);
                        desiredAccount.setBalance(desiredAccount.getBalance() - commandDTO.getAmount());
                        handler.displayInfo(customer.getName() + "���� " + desiredAccount.getAccountNo() + " ���¿��� " + commandDTO.getAmount() + "�� ����Ͽ����ϴ�.");
                    }

                    break;
                }



            }
        }


        send(commandDTO);
    }
}
