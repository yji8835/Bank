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
// # 05
//*******************************************************************
// Name : PanDeposite
// Type : Class
// Description :  �Ա� ȭ�� �г��� ������ Class �̴�.
//*******************************************************************
public class PanDeposite extends JPanel implements ActionListener
{
    private JLabel Label_Title;

    private JLabel Label_Amount;
    private JTextField Text_Amount;


    private JButton Btn_Deposite;
    private JButton Btn_Close;

    ATMMain MainFrame;


    //*******************************************************************
    // # 05-01
    //*******************************************************************
    // Name : PanDeposite()
    // Type : ������
    // Description :  PanDeposite Class�� ������ ����
    //*******************************************************************
    public PanDeposite(ATMMain parent)
    {
        MainFrame = parent;
        InitGUI();
    }

    //*******************************************************************
    // # 05-02
    //*******************************************************************
    // Name : InitGUI
    // Type : Method
    // Description :  �Ա� ȭ�� �г��� GUI�� �ʱ�ȭ �ϴ� �޼ҵ� ����
    //*******************************************************************
    private void InitGUI()
    {
        setLayout(null);
        setBounds(0,0,480,320);


        Label_Title = new JLabel("�Ա�");
        Label_Title.setBounds(0,0,480,40);
        Label_Title.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Title);

        Label_Amount = new JLabel("�ݾ�");
        Label_Amount.setBounds(0,120,100,20);
        Label_Amount.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Amount);

        Text_Amount = new JTextField();
        Text_Amount.setBounds(100,120,350,20);
        Text_Amount.setEditable(true);
        add(Text_Amount);

        Btn_Deposite = new JButton("�Ա�");
        Btn_Deposite.setBounds(100,250,70,20);
        Btn_Deposite.addActionListener(this);
        add(Btn_Deposite);

        Btn_Close = new JButton("���");
        Btn_Close.setBounds(250,250,70,20);
        Btn_Close.addActionListener(this);
        add(Btn_Close);
    }


    //*******************************************************************
    // # 05-02-01
    //*******************************************************************
    // Name : actionPerformed
    // Type : Listner
    // Description :  �Ա� ��ư, ��� ��ư�� ������ ����
    //                �Ա�, ��� ���� �� ���� ȭ������ ����ǵ��� ����
    //*******************************************************************
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == Btn_Deposite)
        {
            deposit();
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
    // # 05-03
    //*******************************************************************
    // Name : deposit()
    // Type : Method
    // Description :  �Ա� ȭ���� �����͸� ������ �ִ� CommandDTO�� �����ϰ�,
    //                ATMMain�� Send ����� ȣ���Ͽ� ������ �Ա� ��û �޽����� ���� �ϴ� ���.
    //*******************************************************************
    public void deposit() {
        long amount = Long.parseLong(Text_Amount.getText());
        CommandDTO commandDTO = new CommandDTO(RequestType.DEPOSIT, ATMMain.userId, amount);
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
                        if (command.getResponseType() == ResponseType.SUCCESS)
                        {
                            contentText = "�Ա� �Ǿ����ϴ�.";
                            JOptionPane.showMessageDialog(null, contentText, "SUCCESS_MESSAGE", JOptionPane.PLAIN_MESSAGE);
                        }
                        else
                        {

                            contentText = "�Ա� ����! �����ڿ��� �����ϼ���.";
                            JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (IOException e)
                {
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
