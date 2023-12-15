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
// # 06
//*******************************************************************
// Name : PanWithdrawal
// Type : Class
// Description :  ??? ??? ?г??? ?????? Class ???.
//*******************************************************************
public class PanWithdrawal extends JPanel implements ActionListener
{
    private JLabel Label_Title;


    private JLabel Label_Amount;
    private JTextField Text_Amount;


    private JButton Btn_Transfer;
    private JButton Btn_Close;

    ATMMain MainFrame;

    //*******************************************************************
    // # 06-01
    //*******************************************************************
    // Name : PanWithdrawal()
    // Type : ??????
    // Description :  PanDeposite Class?? ?????? ????
    //*******************************************************************
    public PanWithdrawal(ATMMain parent)
    {
        MainFrame = parent;
        InitGUI();
    }
    
    //*******************************************************************
    // # 06-02
    //*******************************************************************
    // Name : InitGUI
    // Type : Method
    // Description :  ??? ??? ?г??? GUI?? ???? ??? ???? ????
    //*******************************************************************
    private void InitGUI()
    {
        setLayout(null);
        setBounds(0,0,480,320);


        Label_Title = new JLabel("???");
        Label_Title.setBounds(0,0,480,40);
        Label_Title.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Title);


        Label_Amount = new JLabel("???");
        Label_Amount.setBounds(0,120,100,20);
        Label_Amount.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Amount);

        Text_Amount = new JTextField();
        Text_Amount.setBounds(100,120,350,20);
        Text_Amount.setEditable(true);
        Text_Amount.setToolTipText("????? ???");
        add(Text_Amount);

        Btn_Transfer = new JButton("???");
        Btn_Transfer.setBounds(100,250,70,20);
        Btn_Transfer.addActionListener(this);
        add(Btn_Transfer);

        Btn_Close = new JButton("???");
        Btn_Close.setBounds(250,250,70,20);
        Btn_Close.addActionListener(this);
        add(Btn_Close);
    }

    //*******************************************************************
    // # 06-02-01
    //*******************************************************************
    // Name : actionPerformed
    // Type : Listner
    // Description :  ??? ???, ??? ????? ?????? ????
    //                ???, ??? ???? ?? ???? ??????? ???????? ????
    //*******************************************************************
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == Btn_Transfer)
        {
            Withdrawal();
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
    // # 06-03
    //*******************************************************************
    // Name : Withdrawal()
    // Type : Method
    // Description :  ??? ????? ??????? ?????? ??? CommandDTO?? ???????,
    //                ATMMain?? Send ????? ?????? ?????? ??? ??? ??????? ???? ??? ???.
    //*******************************************************************
    public void Withdrawal() {
        long amount = Long.parseLong(Text_Amount.getText());
        CommandDTO commandDTO = new CommandDTO(RequestType.WITHDRAW, MainFrame.userId, amount);
        MainFrame.send(commandDTO, new CompletionHandler<Integer, ByteBuffer>()
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
                            contentText = "??? ????????.";
                            JOptionPane.showMessageDialog(null, contentText, "SUCCESS_MESSAGE", JOptionPane.PLAIN_MESSAGE);
                        }
                        else if (command.getResponseType() == ResponseType.INSUFFICIENT)
                        {

                            contentText = "????? ????????";
                            JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
                        }
                        else
                        {
                            contentText = "ERROR";
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
