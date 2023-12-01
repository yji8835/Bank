package bank;

import common.CommandDTO;
import common.ResponseType;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
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
        // ������ ����
        commandDTO.setBalance(customer.getAccount().getBalance());
        commandDTO.setUserAccountNo(customer.getAccount().getAccountNo());
        handler.displayInfo(customer.getAccount().getOwner() + "���� ���� �ܾ��� " + customer.getAccount().getBalance() + "�� �Դϴ�.");
        send(commandDTO);
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
        CustomerVO user = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getAccount().getAccountNo(), commandDTO.getUserAccountNo())).findFirst().get();
        Optional<CustomerVO> receiverOptional = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getAccount().getAccountNo(), commandDTO.getReceivedAccountNo())).findFirst();
        if (!receiverOptional.isPresent()) {
            commandDTO.setResponseType(ResponseType.WRONG_ACCOUNT_NO);
        } else if (receiverOptional.get().getAccount().getAccountNo().equals(user.getAccount().getAccountNo())) {
            commandDTO.setResponseType(ResponseType.WRONG_ACCOUNT_NO);
        } else if (!user.getPassword().equals(commandDTO.getPassword())) {
            commandDTO.setResponseType(ResponseType.WRONG_PASSWORD);
        } else if (user.getAccount().getBalance() < commandDTO.getAmount()) {
            commandDTO.setResponseType(ResponseType.INSUFFICIENT);
        } else {
            CustomerVO receiver = receiverOptional.get();
            commandDTO.setResponseType(ResponseType.SUCCESS);
            user.getAccount().setBalance(user.getAccount().getBalance() - commandDTO.getAmount());
            receiver.getAccount().setBalance(receiver.getAccount().getBalance() + commandDTO.getAmount());
            handler.displayInfo(user.getAccount().getAccountNo() + " ���¿��� " + receiver.getAccount().getAccountNo() + "���·� " + commandDTO.getAmount() + "�� ��ü�Ͽ����ϴ�.");
        }
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
        CustomerVO user = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getAccount().getAccountNo(), commandDTO.getUserAccountNo())).findFirst().get();
        user.getAccount().setBalance(user.getAccount().getBalance() + commandDTO.getAmount());
        commandDTO.setResponseType(ResponseType.SUCCESS);
        handler.displayInfo(user.getName() + "���� " + user.getAccount().getAccountNo() + " ���¿� " + commandDTO.getAmount() + "�� �Ա��Ͽ����ϴ�.");
        send(commandDTO);
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
        CustomerVO user = this.customerList.stream().filter(customerVO -> Objects.equals(customerVO.getAccount().getAccountNo(), commandDTO.getUserAccountNo())).findFirst().get();
        if (user.getAccount().getBalance() < commandDTO.getAmount()) {
            commandDTO.setResponseType(ResponseType.INSUFFICIENT);
        } else {
            commandDTO.setResponseType(ResponseType.SUCCESS);
            user.getAccount().setBalance(user.getAccount().getBalance() - commandDTO.getAmount());
            handler.displayInfo(user.getName() + "���� " + user.getAccount().getAccountNo() + " ���¿��� " + commandDTO.getAmount() + "�� ����Ͽ����ϴ�.");
        }
        send(commandDTO);
    }
}
