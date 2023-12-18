package common;

import java.io.Serializable;

import common.AccountType;
//*******************************************************************
// # 91
//*******************************************************************
// Name : CommandDTO
// Type : Class
// Description :  ATM 과 Sever 사이의 통신 프로토콜을 정의 하기 위해 필요한 DTO(DataTransferObject)이다.
//                생성자와, 오브젝트 내부 데이터 get, set 동작이 구현되어 있다.
//*******************************************************************

@SuppressWarnings("serial")
public class CommandDTO implements Serializable {
    private RequestType requestType;
    private String id;
    private AccountType type;
    private String linkedsaving;
    private double interestrate;
    private long max;
    private String password;
    private String userAccountNo;
    private String receivedAccountNo;
    private long amount;
    private long balance;
    private ResponseType responseType;

    public CommandDTO() {
    }

    public CommandDTO(RequestType requestType) {
        this.requestType = requestType;
    }

    public CommandDTO(ResponseType responseType) {
        this.responseType = responseType;
    }

    public CommandDTO(RequestType requestType, String userAccountNo) {
        this.requestType = requestType;
        this.userAccountNo = userAccountNo;
    }

    public CommandDTO(RequestType requestType, String userAccountNo, long amount) {
        this.requestType = requestType;
        this.userAccountNo = userAccountNo;
        this.amount = amount;
    }

    public CommandDTO(RequestType requestType, String id, String userAccountNo, String password) {
        this.requestType = requestType;
        this.id = id;
        this.userAccountNo = userAccountNo;
        this.password = password;
    }

    public CommandDTO(RequestType requestType, String id, String password) {
        this.requestType = requestType;
        this.id = id;
        this.password = password;
    }

    public CommandDTO(RequestType requestType, String password, String id ,String userAccountNo, String receivedAccountNo, long amount) {
        this.requestType = requestType;
        this.password = password;
        this.id = id;
        this.userAccountNo = userAccountNo;
        this.receivedAccountNo = receivedAccountNo;
        this.amount = amount;
    }

    public CommandDTO(RequestType requestType, String password, String userAccountNo, String receivedAccountNo, long amount) {
        this.requestType = requestType;
        this.password = password;
        this.userAccountNo = userAccountNo;
        this.receivedAccountNo = receivedAccountNo;
        this.amount = amount;
    }

    public CommandDTO(RequestType requestType, String userAccountNo, String receivedAccountNo, long amount, long balance) {
        this.requestType = requestType;
        this.userAccountNo = userAccountNo;
        this.receivedAccountNo = receivedAccountNo;
        this.amount = amount;
        this.balance = balance;
    }

    public CommandDTO(RequestType requestType, String id, String userAccountNo, AccountType type, String linkedsaving, double interestrate, long max) {
        this.requestType = requestType;
        this.id = id;
        this.userAccountNo = userAccountNo;
        this.type = type;
        this.linkedsaving = linkedsaving;
        this.interestrate = interestrate;
        this.max = max;
    }

    public CommandDTO(RequestType requestType, String id, String userAccountNo, long amount) {
        this.requestType = requestType;
        this.id = id;
        this.userAccountNo = userAccountNo;
        this.amount = amount;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {this.type = type;}

    public String getLinkedsaving() {
        return linkedsaving;
    }

    public void setLinkedsavinge(String linkedsaving) {this.linkedsaving = linkedsaving;}

    public double getInterestrate() {return interestrate;}

    public void setInterestrate(double interestrate) {this.interestrate = interestrate;}

    public long getMax() {
        return max;
    }

    public void setMax(long max) {this.max = max;}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserAccountNo() {
        return userAccountNo;
    }

    public void setUserAccountNo(String userAccountNo) {
        this.userAccountNo = userAccountNo;
    }

    public String getReceivedAccountNo() {
        return receivedAccountNo;
    }

    public void setReceivedAccountNo(String receivedAccountNo) {
        this.receivedAccountNo = receivedAccountNo;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    @Override
    public String toString() {
        return "CommandDTO{" +
                "requestType=" + requestType +
                ", id='" + id + '\'' +
                ", password='" + password + '\'' +
                ", userAccountNo='" + userAccountNo + '\'' +
                ", type='" + type + '\'' +
                ", link='" + linkedsaving + '\'' +
                ", interest='" + interestrate + '\'' +
                ", max='" + max + '\'' +
                ", receivedAccountNo='" + receivedAccountNo + '\'' +
                ", amount=" + amount +
                ", balance=" + balance +
                ", responseType=" + responseType +
                '}';
    }
}
