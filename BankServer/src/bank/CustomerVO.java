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
// Description :  �������� ���� �ϱ� ���� �ʿ��� VO(ValueObject)�̴�.
//                �����ڿ�, ������Ʈ ���� ������ get, set ������ �����Ǿ� �ִ�.
//                ������Ʈ ���·� txt�� �����Ҽ� �ֵ��� implements Serializable�� ����
//                ����ȭ �Ǿ��ִ�.
//*******************************************************************
public class CustomerVO implements Serializable {
    private String id;
    private String name;
    private String password;
    private String address;
    private String phone;
    private AccountVO account;
    private ArrayList<AccountVO> accountlist;
    private long totalbalance;

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
        this.accountlist = new ArrayList<>();
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
    } //�Ϻ� �� �Ӽ��� ���� ���� ����, ��ȯ

    public long getNumberofaccounts(){ return accountlist.size();}

    public ArrayList<AccountVO> getAccountlist() {
        return  accountlist;
    }
    public void addaccount(AccountVO account){ //���ο� ������ �߰�
        accountlist.add(account);
    }
    public void removeaccount(AccountVO account){ //���� ������ ����
        accountlist.remove(account);
    }
    public AccountVO findaccount(String own){ //Ư�� ������ ���� �߰�
        for(AccountVO accountVO : accountlist){
            if(accountVO.getOwner().equals(own)){
                return accountVO;
            }
        }
        return null;
    }
    public long getTotalBalance(AccountVO account){ //���������� �ܾ��� ��
        for(AccountVO accountVO : accountlist){
            totalbalance += accountVO.getBalance();
        }
        return totalbalance;
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
                ", accountlist=" + accountlist +
                '}';
    }
}
