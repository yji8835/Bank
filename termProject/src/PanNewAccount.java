import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import bank.AccountVO;
import bank.CheckingAccount;
import bank.CustomerVO;
import bank.SavingsAccount;
import common.AccountType;

public class PanNewAccount extends JPanel implements ActionListener {
    private JPanel AccountType;
    private JLabel Label_CustomerName;
    private JTextField Text_CustomerName;
    private JLabel Label_Account;
    private JTextField Text_Account;
    private JLabel Label_type;
    private JRadioButton Checking, Saving;
    private ButtonGroup TypeGroup;
    private JButton Btn_View;
    private JButton Btn_Close;

    private JLabel Label_Linkedaccount;
    private JTextField Text_Linkedaccount;

    private JLabel Label_Interestrate;
    private JTextField Text_Interestrate;
    private JLabel Label_Max;
    private JTextField Text_Max;

    ManagerMain MainFrame;

    private String id;
    private String name;
    private String accountno;
    private AccountType type;
    private long balance;
    private Date openDate;

    private String linkedSavings;

    private double interestRate;
    private long maxTransferAmountToChecking;
    private List<CustomerVO> customerList = new ArrayList<>();
    private ArrayList<AccountVO> accountlist = new ArrayList<>();


    public PanNewAccount(ManagerMain parent) {
        MainFrame = parent;
        InitGUI();
    }

    private void InitGUI() {
        setLayout(null);
        setBounds(0, 0, 480, 320);

        Label_CustomerName = new JLabel("고객 id");
        Label_CustomerName.setBounds(0, 0, 100, 20);
        Label_CustomerName.setHorizontalAlignment(JLabel.LEFT);
        add(Label_CustomerName);

        Text_CustomerName = new JTextField();
        Text_CustomerName.setBounds(100, 0, 350, 20);
        Text_CustomerName.addActionListener(this);
        add(Text_CustomerName);

        Label_Account = new JLabel("계좌 번호");
        Label_Account.setBounds(0, 50, 100, 20);
        Label_Account.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Account);

        Text_Account = new JTextField();
        Text_Account.setBounds(100, 50, 350, 20);
        Text_Account.setEditable(true);
        add(Text_Account);

        Label_type = new JLabel("계좌 타입");
        Label_type.setBounds(0, 100, 100, 20);
        Label_type.setHorizontalAlignment(JLabel.LEFT);
        add(Label_type);

        AccountType = new JPanel();
        AccountType.setBounds(100, 100, 350, 40);
        Checking = new JRadioButton("당좌");
        Checking.addActionListener(this);
        Saving = new JRadioButton("저축");
        Saving.addActionListener(this);
        TypeGroup = new ButtonGroup();
        TypeGroup.add(Checking);
        TypeGroup.add(Saving);
        AccountType.add(Checking);
        AccountType.add(Saving);
        Checking.doClick();
        add(AccountType);

        Label_Linkedaccount = new JLabel("연결된 계좌");
        Label_Linkedaccount.setBounds(0, 150, 100, 20);
        Label_Linkedaccount.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Linkedaccount);

        Text_Linkedaccount = new JTextField();
        Text_Linkedaccount.setBounds(100, 150, 350, 20);
        Text_Linkedaccount.setEditable(true);
        add(Text_Linkedaccount);

        Label_Interestrate = new JLabel("이자율");
        Label_Interestrate.setBounds(0, 200, 50, 20);
        Label_Interestrate.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Interestrate);

        Text_Interestrate = new JTextField();
        Text_Interestrate.setBounds(50, 200, 50, 20);
        Text_Interestrate.setEditable(true);
        add(Text_Interestrate);

        Label_Max = new JLabel("최대이체한도");
        Label_Max.setBounds(100, 200, 100, 20);
        Label_Max.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Max);

        Text_Max = new JTextField();
        Text_Max.setBounds(200, 200, 150, 20);
        Text_Max.setEditable(true);
        add(Text_Max);


        Btn_View = new JButton("등록");
        Btn_View.setBounds(140, 250, 70, 20);
        Btn_View.addActionListener(this);
        add(Btn_View);

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
        if (e.getSource() == Btn_View) {

            id = Text_CustomerName.getText();
            accountno = Text_Account.getText();
            linkedSavings = Text_Linkedaccount.getText();
            System.out.println(id);
            System.out.println(accountno);

            add_account();

            //savedefaultlist();

        }
        if (Checking.isSelected()) {
            type = common.AccountType.CHECKING;
        }
        if (Saving.isSelected()) {
            type = common.AccountType.SAVINGS;
        }
    }

    private void add_account() {

        customerList = ReadCustomerFile("./Account.txt");
        System.out.println(customerList);
        balance = 0;

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateStr = today.format(formatter);
        openDate = java.sql.Date.valueOf(today);

        for(int i=0; i<customerList.size(); i++) {
            if(customerList.get(i).getId().equals(id)) {
                name = customerList.get(i).getName();
            }
        }

        AccountVO newaccount = new AccountVO(name, accountno, type, balance, openDate);

        if (type == common.AccountType.CHECKING) {

            for(int i=0; i<customerList.size(); i++) {
                if(customerList.get(i).getId().equals(id)) {
                    accountlist = customerList.get(i).getAccountlist();
                }
            }

            for (AccountVO account : accountlist) {

                if (account instanceof SavingsAccount) {

                    SavingsAccount savingAccount = (SavingsAccount) account;
                    System.out.println(savingAccount.getAccount().getAccountNo());
                    System.out.println(linkedSavings);
                    if (savingAccount.getAccount().getAccountNo().equals(linkedSavings)) {

                        newaccount = new CheckingAccount(newaccount, savingAccount);
                    }
                }
            }

        } else if (type == common.AccountType.SAVINGS) {
            interestRate = Double.parseDouble(Text_Interestrate.getText());
            maxTransferAmountToChecking = Long.parseLong(Text_Max.getText());
            newaccount = (new SavingsAccount(newaccount, interestRate, maxTransferAmountToChecking));

        }

        System.out.println(newaccount);


        for(int i=0; i<customerList.size(); i++) {
            if(customerList.get(i).getId().equals(id)) {
                customerList.get(i).addaccount(newaccount);
            }
        }
        System.out.println(customerList);
        SaveCustomerFile(customerList, "./Account.txt");


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
            System.out.println("File not found. read x");
            return null;
        }
    }

    private void savedefaultlist() {
        List<CustomerVO> defaultlist = new Vector<>();
        defaultlist.add(new CustomerVO("202300001","광수","202300001", new AccountVO("광수", "202300001", common.AccountType.CHECKING, 100_000_000, Date.valueOf(LocalDate.now()))));
        defaultlist.add(new CustomerVO("202300002","영철","202300002", new AccountVO("영철", "202300002", common.AccountType.CHECKING, 10_000_000, Date.valueOf(LocalDate.now()))));
        defaultlist.add(new CustomerVO("202300003","영숙","202300003", new AccountVO("영숙", "202300003", common.AccountType.CHECKING, 5_000_000, Date.valueOf(LocalDate.now()))));
        defaultlist.add(new CustomerVO("202300004","옥순","202300004", new AccountVO("옥순", "202300004", common.AccountType.CHECKING, 1_000_000, Date.valueOf(LocalDate.now()))));


        SaveCustomerFile(defaultlist, "./Account.txt");
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
