package atm;

import common.CommandDTO;
import common.RequestType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

//*******************************************************************
// # 03
//*******************************************************************
// Name : PanViewAccount
// Type : Class
// Description :  ������ȸ ȭ�� �г��� ������ Class �̴�.
//*******************************************************************
public class PanViewAccount extends JPanel implements ActionListener
{
    private JLabel Label_Account;
    private  JTextArea Text_Account;
    private JLabel Label_balance;
    private  JTextArea Text_balance;

    private JButton Btn_Close, Btn_View;

    ATMMain MainFrame;
    
    //*******************************************************************
    // # 03-01
    //*******************************************************************
    // Name : PanViewAccount()
    // Type : ������
    // Description :  PanViewAccount Class�� ������ ����
    //*******************************************************************
    public PanViewAccount(ATMMain parent)
    {
        MainFrame = parent;
        InitGUI();
    }
    
    //*******************************************************************
    // # 03-02
    //*******************************************************************
    // Name : InitGUI
    // Type : Method
    // Description :  ������ȸ ȭ�� �г��� GUI�� �ʱ�ȭ �ϴ� �޼ҵ� ����
    //*******************************************************************
    private void InitGUI()
    {
        setLayout(null);
        setBounds(0,0,480,320);

        Label_Account = new JLabel("account");
        Label_Account.setBounds(0,70,100,20);
        Label_Account.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Account);

        Text_Account = new JTextArea();
        Text_Account.setBounds(100,70,350,20);
        Text_Account.setEditable(true);
        add(Text_Account);

        Label_balance = new JLabel("balance");
        Label_balance.setBounds(0,120,100,20);
        Label_balance.setHorizontalAlignment(JLabel.LEFT);
        add(Label_balance);

        Text_balance = new JTextArea();
        Text_balance.setBounds(100,120,350,20);
        Text_balance.setEditable(false);
        add(Text_balance);

        Btn_View = new JButton("view");
        Btn_View.setBounds(150,250,70,20);
        Btn_View.addActionListener(this);
        add(Btn_View);

        Btn_Close = new JButton("close");
        Btn_Close.setBounds(250,250,70,20);
        Btn_Close.addActionListener(this);
        add(Btn_Close);
    }

    //*******************************************************************
    // # 03-02-01
    //*******************************************************************
    // Name : actionPerformed
    // Type : Listner
    // Description :  ��� ��ư�� ������ ����
    //                ��� ���� �� ���� ȭ������ ����ǵ��� ����
    //*******************************************************************
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == Btn_Close)
        {
            this.setVisible(false);
            MainFrame.display("Main");
        }
        if (e.getSource() == Btn_View)
        {
            GetBalance();
        }
    } 
    
    //*******************************************************************
    // # 03-03
    //*******************************************************************
    // Name : GetBalance()
    // Type : Method
    // Description :  ATMMain�� Send ����� ȣ���Ͽ� ������ ������ȸ ��û �޽����� ���� �ϴ� ���.
    //*******************************************************************
    public void GetBalance()
    {
        MainFrame.send(new CommandDTO(RequestType.VIEW, Text_Account.getText()), new CompletionHandler<Integer, ByteBuffer>() {
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
                    SwingUtilities.invokeLater(() -> {
                        //String accountNumber = BankUtils.displayAccountNo(command.getUserAccountNo());
                        //Text_Account.setText(accountNumber);
                        String balance = BankUtils.displayBalance(command.getBalance());
                        Text_balance.setText(balance + "원");
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
            }
        });

    }

}
