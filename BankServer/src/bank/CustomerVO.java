package bank;

import javax.sound.sampled.Line;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

//*******************************************************************
// # 53
//*******************************************************************
// Name : CustomerVO
// Type : Class
// Description :  고객정보를 정의 하기 위해 필요한 VO(ValueObject)이다.
//                생성자와, 오브젝트 내부 데이터 get, set 동작이 구현되어 있다.
//                오브젝트 형태로 txt에 저장할수 있도록 implements Serializable를 통해
//                직렬화 되어있다.
//*******************************************************************
public class CustomerVO implements Serializable {
    private String id;
    private String name;
    private String password;
    private String address;
    private String phone;
    private AccountVO account;
    private LinkedList<AccountVO> accountlist = new LinkedList<>();
    private long totalbalance;
    private long numberofaccounts;
    public CustomerVO() {
    }

    public CustomerVO(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public CustomerVO(String id, String name, String password, AccountVO account) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.account = account;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public AccountVO getAccount() {
        return account;
    }

    public void setAccount(AccountVO account) {
        this.account = account;
    } //일부 고객 속성에 대한 값을 설정, 반환

    public long getNumberofaccounts(){ return numberofaccounts;}

    public void addaccount(AccountVO account){ //새로운 계좌의 추가
        accountlist.add(account);
    }
    public void removeaccount(AccountVO account){ //소유 계좌의 삭제
        accountlist.remove(account);
    }
    public AccountVO findaccount(String own){ //특정 소유의 계좌 발견
        for(AccountVO acnt : accountlist){
            if(acnt.getAccountNo().equals(account.getAccountNo())){
                return acnt;
            }
        }
        return null;
    }
    public void getTotalBalance(AccountVO account){ //소유계좌의 잔액의 합
        for(int i = 0; i < accountlist.size(); i++){
            totalbalance += account.getBalance();
        }
        this.totalbalance = totalbalance;
    }
    @Override
    public String toString() {
        return "CustomerVO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", account=" + account +
                '}';
    }
}
