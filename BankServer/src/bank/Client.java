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
// Description :  BankServer에 접속 요청한 ATM 소켓과의 통신을 담당하는 소켓 Class 구현.
//                메시지 Receive, Send 및 예금, 출금, 계좌 이체 등의 기능이 구현 되어 있다.
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
    // Description :  수신한 메시지의 버퍼를 CommandDTO Class 형태로 형변환 후
    //                요청 정보를 파싱하여 각 요청에 대한 기능을 구현
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
                    // 읽은 데이터 파싱
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
                    // 데이터 보낸 후 다시 읽기 모드로
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
    // Description :  연결된 ATM 소켓에 CommandDTO 형태로 메시지를 전달
    //                CommandDTO 내부에 요청에대한 결과 정보가 존재 해야 한다
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
    // Description :  해당 소켓의 예외발생 혹은 통신 실패시 소켓 해제 기능 구현
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
                    handler.displayInfo("계좌 " + customer.getName() + " 정보 입니다." + checkingAccount);
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

                    handler.displayInfo("계좌 " + customer.getName() + " 정보 입니다." + savingAccount);
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
    // Description : ATM의 Loging 요청 기능 구현
    //               성공, 실패 여부를 응답메시지에 전달
    //*******************************************************************
    private synchronized void login(CommandDTO commandDTO) {
        Optional<CustomerVO> customer = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getId()) && Objects.equals(customerVO.getPassword(), commandDTO.getPassword())).findFirst();

        if (customer.isPresent()) {
            commandDTO.setResponseType(ResponseType.SUCCESS);
            String text = customer.get().getName();
            String text2 = "님이 로그인하였습니다.";
            String text3 = text + text2;
            handler.displayInfo(customer.get().getName() + "님이 로그인하였습니다.");
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
    // Description : ATM의 계좌 조회 요청 기능 구현
    //               계좌 잔액을 응답메시지에 전달
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


            handler.displayInfo(customer.getName() + "님의 계좌 잔액은 " + desiredAccount.getBalance() + "원 입니다.");
            send(commandDTO);
        } else {
        }

    }

    //*******************************************************************
    // # 52-01-03
    //*******************************************************************
    // Name : transfer()
    // Type : method
    // Description : ATM의 계좌 이체기능 구현
    //               성공, 실패 여부를 응답메시지에 전달
    //*******************************************************************
    private synchronized void transfer(CommandDTO commandDTO) {

        //본인 계좌
        CustomerVO customer = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getId(), commandDTO.getId())).findFirst().get();
        String account = commandDTO.getUserAccountNo();
        ArrayList<AccountVO> accountlist = customer.getAccountlist();

        //상대 계좌
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


        if (receiveaccount == null) { //상대 계좌 존재x
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


                    if (desiredAccount.getBalance() >= commandDTO.getAmount()) { //계좌 내에서 출금 가능한 경우
                        desiredAccount.setBalance(desiredAccount.getBalance() - commandDTO.getAmount());
                        receiveaccount.setBalance(receiveaccount.getBalance() + commandDTO.getAmount());
                        commandDTO.setResponseType(ResponseType.SUCCESS);
                        handler.displayInfo(commandDTO.getUserAccountNo() + " 계좌에서 " + commandDTO.getReceivedAccountNo() + "계좌로 " + commandDTO.getAmount() + "원 이체하였습니다.");
                    } else { // 출금이 불가능한 경우

                        //부족한 금액 계산
                        long insufficientAmount = commandDTO.getAmount() - desiredAccount.getBalance();
                        //Savings 계좌 최대 이체 금액 및 잔액 확인
                        long maxTransferAmount = checkingAccount.getLinkedSavings().getMaxTransferAmountToChecking();
                        long savingbalance = checkingAccount.getLinkedSavings().getBalance();

                        boolean transfer_ok = (insufficientAmount<=maxTransferAmount) && (savingbalance>=insufficientAmount);

                        if (transfer_ok) { //Savings에서 자동 이체 가능
                            desiredAccount.setBalance(0);
                            checkingAccount.getLinkedSavings().setBalance(checkingAccount.getLinkedSavings().getBalance() - insufficientAmount);
                            receiveaccount.setBalance(receiveaccount.getBalance() + commandDTO.getAmount());
                            commandDTO.setResponseType(ResponseType.SUCCESS);
                            handler.displayInfo(commandDTO.getUserAccountNo() + " 계좌에서 " + commandDTO.getReceivedAccountNo() + "계좌로 " + (commandDTO.getAmount() -insufficientAmount) + "원 이체하였습니다. 연결된 저축 계좌에서 " + insufficientAmount +"원을 이체하여 보충했습니다.");

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

                    if (desiredAccount.getBalance() < commandDTO.getAmount()) { // 계좌 잔액 < 출금 금액
                        commandDTO.setResponseType(ResponseType.INSUFFICIENT); //출금 불가
                    } else { // 계좌 잔액 < 출금 금액 -> 출금 가능
                        commandDTO.setResponseType(ResponseType.SUCCESS);
                        desiredAccount.setBalance(desiredAccount.getBalance() - commandDTO.getAmount());
                        receiveaccount.setBalance(receiveaccount.getBalance() + commandDTO.getAmount());
                        handler.displayInfo(commandDTO.getUserAccountNo() + " 계좌에서 " + commandDTO.getReceivedAccountNo() + "계좌로 " + commandDTO.getAmount() + "원 이체하였습니다.");
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
    // Description : ATM의 계좌 입금 기능 구현
    //               성공, 실패 여부를 응답메시지에 전달
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


            handler.displayInfo(customer.getName() + "님이 " + desiredAccount.getAccountNo() + " 계좌에 " + commandDTO.getAmount() + "원 입금하였습니다.");
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
    // Description : ATM의 계좌 출금 기능 구현
    //               성공, 실패 여부를 응답메시지에 전달
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


                    if (desiredAccount.getBalance() >= commandDTO.getAmount()) { //계좌 내에서 출금 가능한 경우
                        desiredAccount.setBalance(desiredAccount.getBalance() - commandDTO.getAmount());
                        commandDTO.setResponseType(ResponseType.SUCCESS);
                        handler.displayInfo(customer.getName() + "님이 " + desiredAccount.getAccountNo() + " 계좌에서 " + commandDTO.getAmount() + "원 출금하였습니다.");
                    } else { // 출금이 불가능한 경우

                        //부족한 금액 계산
                        long insufficientAmount = commandDTO.getAmount() - desiredAccount.getBalance();
                        //Savings 계좌 최대 이체 금액 및 잔액 확인
                        long maxTransferAmount = checkingAccount.getLinkedSavings().getMaxTransferAmountToChecking();
                        long savingbalance = checkingAccount.getLinkedSavings().getBalance();

                        boolean transfer_ok = (insufficientAmount<=maxTransferAmount) && (savingbalance>=insufficientAmount);

                        if (transfer_ok) { //Savings에서 자동 이체 가능
                            desiredAccount.setBalance(0);
                            checkingAccount.getLinkedSavings().setBalance(checkingAccount.getLinkedSavings().getBalance() - insufficientAmount);

                            commandDTO.setResponseType(ResponseType.SUCCESS);
                            handler.displayInfo(customer.getName() + "님이 " + desiredAccount.getAccountNo() + " 계좌에서 " + (commandDTO.getAmount() -insufficientAmount) + "원 출금하였습니다. 연결된 저축 계좌에서 " + insufficientAmount + "원을 이체하여 보충했습니다.");
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

                    if (desiredAccount.getBalance() < commandDTO.getAmount()) { // 계좌 잔액 < 출금 금액
                        commandDTO.setResponseType(ResponseType.INSUFFICIENT); //출금 불가
                    } else { // 계좌 잔액 < 출금 금액 -> 출금 가능
                        commandDTO.setResponseType(ResponseType.SUCCESS);
                        desiredAccount.setBalance(desiredAccount.getBalance() - commandDTO.getAmount());
                        handler.displayInfo(customer.getName() + "님이 " + desiredAccount.getAccountNo() + " 계좌에서 " + commandDTO.getAmount() + "원 출금하였습니다.");
                    }

                    break;
                }



            }
        }


        send(commandDTO);
    }
}
