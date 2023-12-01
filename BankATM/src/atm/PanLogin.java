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
// # 02
//*******************************************************************
// Name : PanLogin
// Type : Class
// Description :  �α��� ȭ�� �г��� ������ Class �̴�.
//*******************************************************************
public class PanLogin extends JPanel implements ActionListener
{
    private JLabel Label_Title;

    private JLabel Label_ID;
    private JTextField Text_ID;


    private JLabel Label_Password;
    private JTextField Text_Password;


    private JButton Btn_Transfer;
    private JButton Btn_Close;

    ATMMain MainFrame;
    private BankServiceHandler handler;

    //*******************************************************************
    // # 02-01
    //*******************************************************************
    // Name : PanLogin()
    // Type : ������
    // Description :  PanLogin Class�� ������ ����
    //*******************************************************************
    public PanLogin(ATMMain parent)
    {
        MainFrame = parent;
        InitGUI();
    }
    //*******************************************************************
    // # 02-02
    //*******************************************************************
    // Name : InitGUI
    // Type : Method
    // Description :  �α��� ȭ�� �г��� GUI�� �ʱ�ȭ �ϴ� �޼ҵ� ����
    //*******************************************************************
    private void InitGUI()
    {
        setLayout(null);
        setBounds(0,0,480,320);


        Label_Title = new JLabel("�α���");
        Label_Title.setBounds(0,0,480,40);
        Label_Title.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Title);

        Label_ID = new JLabel("���̵�");
        Label_ID.setBounds(0,60,100,20);
        Label_ID.setHorizontalAlignment(JLabel.LEFT);
        add(Label_ID);

        Text_ID = new JTextField();
        Text_ID.setBounds(100,60,350,20);
        Text_ID.setEditable(true);
        add(Text_ID);


        Label_Password = new JLabel("��й�ȣ");
        Label_Password.setBounds(0,100,100,20);
        Label_Password.setHorizontalAlignment(JLabel.LEFT);
        add(Label_Password);

        Text_Password = new JTextField();
        Text_Password.setBounds(100,100,350,20);
        Text_Password.setEditable(true);
        add(Text_Password);


        Btn_Transfer = new JButton("�α���");
        Btn_Transfer.setBounds(100,250,70,20);
        Btn_Transfer.addActionListener(this);
        add(Btn_Transfer);

        Btn_Close = new JButton("���");
        Btn_Close.setBounds(250,250,70,20);
        Btn_Close.addActionListener(this);
        add(Btn_Close);
    }


    //*******************************************************************
    // # 02-02-01
    //*******************************************************************
    // Name : actionPerformed
    // Type : Listner
    // Description :  �α��� ��ư, ��� ��ư�� ������ ����
    //                �α���, ��� ���� �� ���� ȭ������ ����ǵ��� ����
    //*******************************************************************
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == Btn_Transfer)
        {
            Login();
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
    // # 02-03
    //*******************************************************************
    // Name : Login()
    // Type : Method
    // Description :  �α��� ȭ���� �����͸� ������ �ִ� CommandDTO�� �����ϰ�,
    //                ATMMain�� Send ����� ȣ���Ͽ� ������ �α��� ��û �޽����� ���� �ϴ� ���.
    //*******************************************************************
    public void Login()
    {
        String id = Text_ID.getText();
        String password = Text_Password.getText();
        MainFrame.userId = id;
        MainFrame.send(new CommandDTO(RequestType.LOGIN, id, password), new CompletionHandler<Integer, ByteBuffer>()
        {
            @Override
            public void completed(Integer result, ByteBuffer attachment)
            {
                if (result == -1)
                {
                    return;
                }
                attachment.flip();
                try
                {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(attachment.array());
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    CommandDTO command = (CommandDTO) objectInputStream.readObject();
                    SwingUtilities.invokeLater(() ->
                    {
                        String contentText = null;

                        if (command.getResponseType() == ResponseType.SUCCESS)
                        {
                            MainFrame.userId = id;
                            contentText = "�α��εǾ����ϴ�.";
                            JOptionPane.showMessageDialog(null, contentText, "SUCCESS_MESSAGE", JOptionPane.PLAIN_MESSAGE);
                            setVisible(false);
                            MainFrame.display("Main");
                        }
                        else if (command.getResponseType() == ResponseType.FAILURE)
                        {
                            contentText = "���̵� �Ǵ� ��й�ȣ�� ��ġ���� �ʽ��ϴ�.";
                            JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
                        } else
                        {
                            contentText = "ERROR.";
                            JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
                        }
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
