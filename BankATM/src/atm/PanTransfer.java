package atm;

import common.CommandDTO;
import common.RequestType;
import common.ResponseType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

//*******************************************************************
// # 04
//*******************************************************************
// Name : PanTransfer
// Type : Class
// Description :  ���� ��ü �г��� ������ Class �̴�.
//*******************************************************************

public class PanTransfer extends JPanel implements ActionListener
{
    private JLabel Label_RecvAccount;
    private  JTextField Text_RecvAccount;


    private JLabel Label_Amount;
    private JTextField Text_Amount;


    private JLabel Label_Password;
    private JTextField Text_Password;


    private JButton Btn_Transfer;
    private JButton Btn_Close;

    ATMMain MainFrame;

    //*******************************************************************
    // # 04-01
    //*******************************************************************
    // Name : PanTransfer()
    // Type : ������
    // Description :  PanTransfer Class�� ������ ����
    //*******************************************************************
    public PanTransfer(ATMMain parent)
    {
        MainFrame = parent;
        InitGUI();
    }
    
    //*******************************************************************
    // # 04-02
    //*******************************************************************
    // Name : InitGUI
    // Type : Method
    // Description :  ���� ��ü ȭ�� �г��� GUI�� �ʱ�ȭ �ϴ� �޼ҵ� ����
    //*******************************************************************
    private void InitGUI()
    {
        setLayout(null);
        setBounds(0,0,480,320);

        Label_RecvAccount = new JLabel("�޴� �� ���¹�ȣ");
        Label_RecvAccount.setBounds(0,20,100,20);
        Label_RecvAccount.setHorizontalAlignment(JLabel.LEFT);
        add(Label_RecvAccount);

        Text_RecvAccount = new JTextField();
        Text_RecvAccount.setBounds(100,20,350,20);
        Text_RecvAccount.setEditable(true);
        Text_RecvAccount.setToolTipText("���ڸ� �Է�");
        add(Text_RecvAccount);

        Label_Amount = new JLabel("��ü�ݾ�");
        Label_Amount.setBounds(0,70,100,20);
        Label_Amount.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Amount);

        Text_Amount = new JTextField();
        Text_Amount.setBounds(100,70,350,20);
        Text_Amount.setEditable(true);
        Text_Amount.setToolTipText("���ڸ� �Է�");
        add(Text_Amount);

        Label_Password = new JLabel("��й�ȣ");
        Label_Password.setBounds(0,120,100,20);
        Label_Password.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Password);

        Text_Password = new JTextField();
        Text_Password.setBounds(100,120,350,20);
        Text_Password.setEditable(true);
        add(Text_Password);

        Btn_Transfer = new JButton("��ü");
        Btn_Transfer.setBounds(100,250,70,20);
        Btn_Transfer.addActionListener(this);
        add(Btn_Transfer);

        Btn_Close = new JButton("�ݱ�");
        Btn_Close.setBounds(250,250,70,20);
        Btn_Close.addActionListener(this);
        add(Btn_Close);
    }


    //*******************************************************************
    // # 04-02-01
    //*******************************************************************
    // Name : actionPerformed
    // Type : Listner
    // Description :  ��ü ��ư, ��� ��ư�� ������ ����
    //                ��ü, ��� ���� �� ���� ȭ������ ����ǵ��� ����
    //*******************************************************************
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == Btn_Transfer)
        {
            Transfer();
            this.setVisible(false);
            MainFrame.display("Main");
        }
        if (e.getSource() == Btn_Close)
        {
            this.setVisible(false);
            MainFrame.display("Main");
        }
    }

    //*******************************************************************
    // # 04-03
    //*******************************************************************
    // Name : Transfer()
    // Type : Method
    // Description :  ���� ��ü ȭ���� �����͸� ������ �ִ� CommandDTO�� �����ϰ�,
    //                ATMMain�� Send ����� ȣ���Ͽ� ������ ������ü ��û �޽����� ���� �ϴ� ���.
    //*******************************************************************
    public void Transfer()
    {
        String receiveAccountNo = Text_RecvAccount.getText();
        long amount = Long.parseLong(Text_Amount.getText());
        String password = Text_Password.getText();

        CommandDTO commandDTO = new CommandDTO(RequestType.TRANSFER, password, MainFrame.userId, receiveAccountNo, amount);
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
                    SwingUtilities.invokeLater(() ->
                    {
                        String contentText = null;

                        if (command.getResponseType() == ResponseType.INSUFFICIENT)
                        {
                            contentText = "�ܾ��� �����մϴ�.";
                            JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
                        }
                        else if (command.getResponseType() == ResponseType.WRONG_ACCOUNT_NO)
                        {
                            contentText = "���¹�ȣ�� �������� �ʽ��ϴ�.";
                            JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
                        }
                        else if (command.getResponseType() == ResponseType.WRONG_PASSWORD)
                        {
                            contentText = "��й�ȣ�� ��ġ���� �ʽ��ϴ�.";
                            JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
                        }
                        else
                        {
                            contentText = "��ü �Ǿ����ϴ�.";
                            JOptionPane.showMessageDialog(null, contentText, "SUCCESS_MESSAGE", JOptionPane.PLAIN_MESSAGE);
                        }
                    });
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
            }
        });
    }

}
