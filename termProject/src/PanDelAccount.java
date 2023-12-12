import bank.AccountVO;
import bank.CheckingAccount;
import bank.CustomerVO;
import bank.SavingsAccount;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PanDelAccount extends JPanel implements ActionListener {
    private JLabel Label_CustomerName, Label_Account, Label_PassWord;
    private JTextField Text_CustomerName, Text_Account, Text_PassWord;

    private JButton Btn_Del;
    private JButton Btn_Close;

    private String name, password, accountno;

    private List<CustomerVO> customerList;


    ManagerMain MainFrame;

    public PanDelAccount(ManagerMain parent) {
        MainFrame = parent;
        InitGUI();
    }

    private void InitGUI() {
        setLayout(null);
        setBounds(0, 0, 480, 320);

        Label_CustomerName = new JLabel("고객 id");
        Label_CustomerName.setBounds(0, 50, 100, 20);
        Label_CustomerName.setHorizontalAlignment(JLabel.LEFT);
        add(Label_CustomerName);

        Text_CustomerName = new JTextField();
        Text_CustomerName.setBounds(100, 50, 350, 20);
        Text_CustomerName.addActionListener(this);
        add(Text_CustomerName);

        Label_Account = new JLabel("계좌 번호");
        Label_Account.setBounds(0, 100, 100, 20);
        Label_Account.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Account);

        Text_Account = new JTextField();
        Text_Account.setBounds(100, 100, 350, 20);
        Text_Account.setEditable(true);
        add(Text_Account);

        Label_PassWord = new JLabel("비밀번호");
        Label_PassWord.setBounds(0, 150, 100, 20);
        Label_PassWord.setHorizontalAlignment(JLabel.LEFT);
        add(Label_PassWord);

        Text_PassWord = new JTextField();
        Text_PassWord.setBounds(100, 150, 350, 20);
        Text_PassWord.setEditable(true);
        add(Text_PassWord);

        Btn_Del = new JButton("삭제");
        Btn_Del.setBounds(140, 250, 70, 20);
        Btn_Del.addActionListener(this);
        add(Btn_Del);

        Btn_Close = new JButton("닫기");
        Btn_Close.setBounds(250, 250, 70, 20);
        Btn_Close.addActionListener(this);
        add(Btn_Close);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Btn_Close) {
            this.setVisible(false);
            MainFrame.display("Main");
        }
        if (e.getSource() == Btn_Del) {
            deleteaccount();
        }
    }

    public void deleteaccount() {
        customerList = ReadCustomerFile("./Account.txt");

        for (int i = 0; i < customerList.size(); i++) {
            name = Text_CustomerName.getText();
            password = Text_PassWord.getText();
            accountno = Text_Account.getText();

            if(customerList.get(i).getId().equals(name) && customerList.get(i).getPassword().equals(password)) {
                ArrayList<AccountVO> accountlist = customerList.get(i).getAccountlist();

                for (AccountVO account : accountlist) {

                    if (account instanceof CheckingAccount) {
                        CheckingAccount checkingAccount = (CheckingAccount)account;
                        if (accountno.equals(checkingAccount.getAccount().getAccountNo())) {
                            accountlist.remove(checkingAccount);
                            System.out.println(accountlist);
                            SaveCustomerFile(customerList, "./Account.txt");
                            return;
                        }
                    }
                    if (account instanceof SavingsAccount) {
                        SavingsAccount savingAccount = (SavingsAccount) account;
                        if (accountno.equals(savingAccount.getAccount().getAccountNo())) {
                            accountlist.remove(savingAccount);
                            System.out.println(accountlist);
                            SaveCustomerFile(customerList, "./Account.txt");
                            return;
                        }
                    }

                }
            }
        }

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
            System.out.println("File not found.");
            return null;
        }
    }

    public void SaveCustomerFile(List<CustomerVO> customers, String filePath)
    {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath)))
        {
            oos.writeObject(customers);
            System.out.println("Objects saved to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}