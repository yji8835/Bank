import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import bank.AccountVO;
import bank.CheckingAccount;
import bank.CustomerVO;
import bank.SavingsAccount;
import org.w3c.dom.Text;

public class PanViewCustomer  extends JPanel implements ActionListener
{
    private JLabel Label_CustomerName, Label_ID, Label_PassWord, Label_Phone, Label_Address, Label_accountbox;
    private  JTextField Text_CustomerName;
    private JTextField Text_ID;
    private JTextField Text_Phone;
    private JTextField Text_Address;
    private JPasswordField Text_PassWord;
    private JComboBox accountbox;
    private JButton Btn_View;
    private JButton Btn_Close;
    private JButton Btn_Return;
    private List<CustomerVO> customerList = new ArrayList<>();
    private ArrayList<AccountVO> Accountlist = new ArrayList<>();
    private String name;
    private String id;
    private String password;

    ManagerMain MainFrame;
    public PanViewCustomer(ManagerMain parent)
    {
        MainFrame = parent;
        InitGUI();
    }
    public List<CustomerVO> ReadCustomerFile(String filePath)
    {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath)))
        {
            List<CustomerVO> customers = (List<CustomerVO>) ois.readObject();
            System.out.println("Objects read from " + filePath);
            return customers;
        }
        catch (IOException | ClassNotFoundException e)
        {
            // 파일이 존재하지 않을 때 초기 데이터로 리스트를 초기화

            System.out.println("File not found. Initializing with default data.");
            return null;
        }
    }
    private void InitGUI()
    {
        setLayout(null);
        setBounds(0,0,480,320);

        Label_CustomerName = new JLabel("고객 이름");
        Label_CustomerName.setBounds(0,30,100,20);
        Label_CustomerName.setHorizontalAlignment(JLabel.LEFT);
        add(Label_CustomerName);

        Text_CustomerName = new JTextField();
        Text_CustomerName.setBounds(100,30,350,20);
        Text_CustomerName.addActionListener(this);
        add(Text_CustomerName);

        Label_ID = new JLabel("아이디");
        Label_ID.setBounds(0,70,100,20);
        Label_ID.setHorizontalAlignment(JLabel.LEFT);
        add(Label_ID);

        Text_ID = new JTextField();
        Text_ID.setBounds(100,70,350,20);
        Text_ID.setEditable(true);
        add(Text_ID);

        Label_PassWord = new JLabel("비밀 번호");
        Label_PassWord.setBounds(0,110,100,20);
        Label_PassWord.setHorizontalAlignment(JLabel.LEFT);
        add(Label_PassWord);

        Text_PassWord = new JPasswordField();
        Text_PassWord.setBounds(100,110,350,20);
        Text_PassWord.setEditable(true);
        add(Text_PassWord);

        Btn_View = new JButton("조회");
        Btn_View.setBounds(140,250,70,20);
        Btn_View.addActionListener(this);
        add(Btn_View);

        Btn_Close = new JButton("취소");
        Btn_Close.setBounds(250,250,70,20);
        Btn_Close.addActionListener(this);
        add(Btn_Close);
        //////////////////////////////////////////
        Label_Phone = new JLabel("전화번호");
        Label_Phone.setBounds(0,150,100,20);
        Label_Phone.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Phone);

        Text_Phone = new JTextField();
        Text_Phone.setBounds(100,150,350,20);
        Text_Phone.setEditable(false);
        add(Text_Phone);

        Label_Address = new JLabel("주소");
        Label_Address.setBounds(0,190,100,20);
        Label_Address.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Address);

        Text_Address = new JTextField();
        Text_Address.setBounds(100,190,350,20);
        Text_Address.setEditable(false);
        add(Text_Address);

        Label_accountbox = new JLabel("계좌 종류");
        Label_accountbox.setBounds(0, 230, 350, 20);
        Label_accountbox.setHorizontalAlignment(JLabel.LEFT);
        add(Label_accountbox);

        accountbox = new JComboBox();
        accountbox.setBounds(100, 230, 350, 20);
        accountbox.setEditable(false);
        add(accountbox);


        Btn_Return = new JButton("이전 화면으로");
        Btn_Return.setBounds(130,250,90,20);
        Btn_Return.addActionListener(this);
        add(Btn_Return);

        Label_Phone.setVisible(false);
        Label_Address.setVisible(false);
        Text_Address.setVisible(false);
        Text_Phone.setVisible(false);
        Label_accountbox.setVisible(false);
        accountbox.setVisible(false);
        Btn_Return.setVisible(false);
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Btn_Close) {
            this.setVisible(false);
            MainFrame.display("Main");

        } else if (e.getSource() == Btn_View) {
            customerList = ReadCustomerFile("./Account.txt");
            name = Text_CustomerName.getText();
            id = Text_ID.getText();
            password = Text_PassWord.getText();

            block_search();
            display_result();
            view_customer();

        } else if (e.getSource() == Btn_Return) {
            block_result();
            display_search();
            Text_ID.setText("");
            Text_CustomerName.setText("");
            Text_PassWord.setText("");
        }
    }
    private void block_search(){
        Btn_View.setVisible(false);
    }
    private void display_search(){
        Text_ID.setEditable(true);
        Text_CustomerName.setEditable(true);
        Text_PassWord.setEditable(true);
        Btn_View.setVisible(true);
    }
    private void display_result(){
        Text_ID.setEditable(false);
        Text_CustomerName.setEditable(false);
        Text_PassWord.setEditable(false);

        Label_Phone.setVisible(true);
        Label_Address.setVisible(true);
        Text_Address.setVisible(true);
        Text_Phone.setVisible(true);
        Label_accountbox.setVisible(true);
        accountbox.setVisible(true);
        Btn_Return.setVisible(true);
    }
    private void block_result(){
        Label_Phone.setVisible(false);
        Label_Address.setVisible(false);
        Text_Address.setVisible(false);
        Text_Phone.setVisible(false);
        Text_Address.setText("");
        Text_Phone.setText("");
        Label_accountbox.setVisible(false);
        accountbox.setVisible(false);
        Btn_Return.setVisible(false);
        accountbox.removeAllItems();
    }
    private void view_customer(){
        if(name == null || id == null || password == null) {
            return;
        }
        for(int i = 0; i < customerList.size(); i++){
            if(customerList.get(i).getName().equals(name)
                && customerList.get(i).getId().equals(id)
                && customerList.get(i).getPassword().equals(password)){
                Accountlist = customerList.get(i).getAccountlist();
                if(customerList.get(i).getPhone() == null){
                    Text_Phone.setText("저장되지 않았습니다.");
                } else{
                    Text_Phone.setText(customerList.get(i).getPhone());
                }
                if(customerList.get(i).getAddress() == null){
                    Text_Address.setText("저장되지 않았습니다.");
                }else {
                    Text_Address.setText(customerList.get(i).getAddress());
                }

                for (AccountVO account : Accountlist) {
                    accountbox.addItem(account);// Assuming AccountVO has a meaningful toString method
                }
            }
        }
    }
}

