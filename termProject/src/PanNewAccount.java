import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
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
import common.CommandDTO;
import common.RequestType;
import common.ResponseType;

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
    private long balance = 0;
    private Date openDate;

    private String linkedSavings = "";
    private double interestRate = 0;
    private long maxTransferAmountToChecking = 0;
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
        if ((e.getSource() == Btn_View) && Checking.isSelected()) {
            id = Text_CustomerName.getText();
            accountno = Text_Account.getText();
            type = common.AccountType.CHECKING;
            if (Text_Linkedaccount.getText() != null) {
                linkedSavings = Text_Linkedaccount.getText();
                interestRate = 0;
                maxTransferAmountToChecking = 0;
                newaccount();
            }
        }

        if ((e.getSource() == Btn_View) && Saving.isSelected()) {
            id = Text_CustomerName.getText();
            accountno = Text_Account.getText();
            type = common.AccountType.SAVINGS;
            if ((Text_Interestrate.getText() != null) && (Text_Max.getText() != null)) {
                linkedSavings = "";
                interestRate = Double.parseDouble(Text_Interestrate.getText());
                maxTransferAmountToChecking = Long.parseLong(Text_Max.getText());
                newaccount();
            }
        }
    }

    public void newaccount() {
        CommandDTO commandDTO = new CommandDTO(RequestType.NEWACCOUNT, id, accountno, type, linkedSavings, interestRate, maxTransferAmountToChecking);
        System.out.println(commandDTO);
        MainFrame.send(commandDTO, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (result == -1) {
                    return;
                }
                attachment.flip();
                try {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(attachment.array());
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    CommandDTO command = (CommandDTO) objectInputStream.readObject();
                    System.out.println("3");

                    SwingUtilities.invokeLater(() ->
                    {
                        String contentText = "";
                        if (command.getResponseType() == ResponseType.SUCCESS)
                        {
                            contentText = "계좌가 생성되었습니다.";
                            JOptionPane.showMessageDialog(null, contentText, "SUCCESS_MESSAGE", JOptionPane.PLAIN_MESSAGE);
                        }

                    });
                } catch (IOException e)
                {
                    System.out.println("????");
                    e.printStackTrace();
                } catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
            @Override
            public void failed(Throwable exc, ByteBuffer attachment)
            {
            }
        });
    }

}
