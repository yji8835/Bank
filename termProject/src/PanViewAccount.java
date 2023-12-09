import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import bank.AccountVO;
import bank.CheckingAccount;
import bank.CustomerVO;
import bank.SavingsAccount;
import common.AccountType;

public class PanViewAccount extends JPanel implements ActionListener
{
    private JLabel Label_CustomerName, Label_Account, Label_type, Label_balance, Label_InterestRate, Label_linkedSavings;
    private  JTextField Text_CustomerName, Text_Account, Text_type, Text_balance, Text_InterestRate, Text_linkedSavings;

    private JButton Btn_View;
    private JButton Btn_Close;
    private JButton Btn_Return;

    private List<CustomerVO> customerList;
    private String name;
    private String accountno;
    private ArrayList<AccountVO> accountlist;

    ManagerMain MainFrame;
    public PanViewAccount(ManagerMain parent)
    {
        MainFrame = parent;
        InitGUI();

    }

    public List<CustomerVO> ReadCustomerFile(String filePath)
    {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./Account.txt")))
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
        setBounds(0, 0, 480, 320);

        Label_CustomerName = new JLabel("고객 id");
        Label_CustomerName.setBounds(0,50,100,20);
        Label_CustomerName.setHorizontalAlignment(JLabel.LEFT);
        add(Label_CustomerName);

        Text_CustomerName = new JTextField();
        Text_CustomerName.setBounds(100,50,350,20);
        Text_CustomerName.addActionListener(this);
        add(Text_CustomerName);

        Label_Account = new JLabel("계좌 번호");
        Label_Account.setBounds(0,100,100,20);
        Label_Account.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Account);

        Text_Account = new JTextField();
        Text_Account.setBounds(100,100,350,20);
        Text_Account.setEditable(true);
        add(Text_Account);

        Btn_View = new JButton("조회");
        Btn_View.setBounds(140,250,70,20);
        Btn_View.addActionListener(this);
        add(Btn_View);

        Btn_Close = new JButton("닫기");
        Btn_Close.setBounds(250,250,70,20);
        Btn_Close.addActionListener(this);
        add(Btn_Close);

//////////////////////////////////////////////////////////////////////
        Label_type = new JLabel("계좌 종류");
        Label_type.setBounds(0,50,100,20);
        Label_type.setHorizontalAlignment(JLabel.LEFT);
        add(Label_type);

        Text_type = new JTextField();
        Text_type.setBounds(100,50,350,20);
        Text_type.setText("type");
        Text_type.setEditable(false);
        Text_type.addActionListener(this);
        add(Text_type);

        Label_balance = new JLabel("잔액");
        Label_balance.setBounds(0,100,100,20);
        Label_balance.setHorizontalAlignment(JLabel.LEFT);
        add(Label_balance);

        Text_balance = new JTextField();
        Text_balance.setBounds(100,100,350,20);
        Text_balance.setText("balance");
        Text_balance.setEditable(false);
        Text_balance.addActionListener(this);
        add(Text_balance);

        Label_InterestRate = new JLabel("이자율");
        Label_InterestRate.setBounds(0,150,100,20);
        Label_InterestRate.setHorizontalAlignment(JLabel.LEFT);
        add(Label_InterestRate);

        Text_InterestRate = new JTextField();
        Text_InterestRate.setBounds(100,150,350,20);
        Text_InterestRate.setText("interestrate");
        Text_InterestRate.setEditable(false);
        Text_InterestRate.addActionListener(this);
        add(Text_InterestRate);

        Label_linkedSavings = new JLabel("연결 계좌");
        Label_linkedSavings.setBounds(0,200,100,20);
        Label_linkedSavings.setHorizontalAlignment(JLabel.LEFT);
        add(Label_linkedSavings);

        Text_linkedSavings = new JTextField();
        Text_linkedSavings.setBounds(100,200,350,20);
        Text_linkedSavings.setText("linkedSavings");
        Text_linkedSavings.setEditable(false);
        Text_linkedSavings.addActionListener(this);
        add(Text_linkedSavings);

        Btn_Return = new JButton("이전 화면으로");
        Btn_Return.setBounds(130,250,90,20);
        Btn_Return.addActionListener(this);
        add(Btn_Return);

        Label_type.setVisible(false);
        Label_balance.setVisible(false);
        Label_InterestRate.setVisible(false);
        Label_linkedSavings.setVisible(false);
        Text_type.setVisible(false);
        Text_balance.setVisible(false);
        Text_InterestRate.setVisible(false);
        Text_linkedSavings.setVisible(false);
        Btn_Return.setVisible(false);


    }
    public void actionPerformed(ActionEvent e)
    {
        Text_type.setText("");
        Text_balance.setText("");

        if (e.getSource() == Btn_Close)
        {

            Text_CustomerName.setText("");
            Text_Account.setText("");
            Label_CustomerName.setVisible(true);
            Label_Account.setVisible(true);
            Text_Account.setVisible(true);
            Text_CustomerName.setVisible(true);
            Btn_View.setVisible(true);
////////////////////////////////////////////////////////
            Label_type.setVisible(false);
            Label_balance.setVisible(false);
            Label_InterestRate.setVisible(false);
            Label_linkedSavings.setVisible(false);
            Text_type.setVisible(false);
            Text_balance.setVisible(false);
            Text_InterestRate.setVisible(false);
            Text_linkedSavings.setVisible(false);
            Btn_Return.setVisible(false);


            this.setVisible(false);
            MainFrame.display("Main");

        }
        if (e.getSource() == Btn_View)
        {
            customerList = ReadCustomerFile("./Account.txt");
            name = Text_CustomerName.getText();
            accountno = Text_Account.getText();

            Text_CustomerName.setText("");
            Text_Account.setText("");
            block_searchbox();

            view_account();

            display_result();

        }
        if (e.getSource() == Btn_Return)
        {
            display_searchbox();
            block_result();
        }
    }

    private void block_searchbox() {
        Label_CustomerName.setVisible(false);
        Label_Account.setVisible(false);
        Text_Account.setVisible(false);
        Text_CustomerName.setVisible(false);
        Btn_View.setVisible(false);
    }

    private void block_result() {
        Label_type.setVisible(false);
        Label_balance.setVisible(false);
        Label_InterestRate.setVisible(false);
        Label_linkedSavings.setVisible(false);
        Text_type.setVisible(false);
        Text_balance.setVisible(false);
        Text_InterestRate.setVisible(false);
        Text_linkedSavings.setVisible(false);
        Btn_Return.setVisible(false);
    }

    private void display_searchbox() {
        Label_CustomerName.setVisible(true);
        Label_Account.setVisible(true);
        Text_Account.setVisible(true);
        Text_CustomerName.setVisible(true);
        Btn_View.setVisible(true);
    }

    private void display_result() {
        Label_type.setVisible(true);
        Label_balance.setVisible(true);
        Label_InterestRate.setVisible(true);
        Label_linkedSavings.setVisible(true);
        Text_type.setVisible(true);
        Text_balance.setVisible(true);
        Text_InterestRate.setVisible(true);
        Text_linkedSavings.setVisible(true);
        Btn_Return.setVisible(true);
    }

    private void view_account() {

        if (name==null || accountno==null) {
            return;
        }

        for(int i=0; i< customerList.size(); i++) {

            if(customerList.get(i).getId().equals(name)) {
                System.out.println("1");
                accountlist = customerList.get(i).getAccountlist();
            }
        }
        for (AccountVO account : accountlist) {

            if (account instanceof CheckingAccount) {
                CheckingAccount checkingAccount = (CheckingAccount)account;
                if (accountno.equals(checkingAccount.getAccount().getAccountNo())) {
                    Text_type.setText(checkingAccount.getAccount().getType()+"");
                    Text_balance.setText(checkingAccount.getAccount().getBalance()+"");
                    Text_InterestRate.setText(0+"");
                    Text_linkedSavings.setText(checkingAccount.getLinkedSavings().getAccount().getAccountNo()+"");
                }
            }
            if (account instanceof SavingsAccount) {
                SavingsAccount savingAccount = (SavingsAccount) account;
                if (accountno.equals(savingAccount.getAccount().getAccountNo())) {
                    Text_type.setText(savingAccount.getAccount().getType()+"");
                    Text_balance.setText(savingAccount.getAccount().getBalance()+"");
                    Text_InterestRate.setText(savingAccount.getInterestRater()+"");
                    Text_linkedSavings.setText("none");
                }
            }

        }

    }



}
