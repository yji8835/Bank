import bank.AccountVO;
import bank.CheckingAccount;
import bank.CustomerVO;
import bank.SavingsAccount;
import common.CommandDTO;
import common.RequestType;
import common.ResponseType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.List;

public class PanDelAccount extends JPanel implements ActionListener {
    private JLabel Label_CustomerName, Label_Account, Label_PassWord;
    private JTextField Text_CustomerName, Text_Account, Text_PassWord;

    private JButton Btn_Del;
    private JButton Btn_Close;

    private String id, password, accountno;

    private List<CustomerVO> customerList = new ArrayList<>();


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
            if (Text_CustomerName.getText() != null && Text_Account.getText() != null && Text_PassWord.getText() != null) {
                id = Text_CustomerName.getText();
                accountno = Text_Account.getText();
                password = Text_PassWord.getText();
                delaccount();
            }
        }
    }

    public void delaccount() {
        CommandDTO commandDTO = new CommandDTO(RequestType.DELETEACCOUNT, id, accountno, password);
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
                    SwingUtilities.invokeLater(() ->
                    {
                        String contentText = "";
                        if (command.getResponseType() == ResponseType.SUCCESS) {
                            contentText = "계좌가 삭제되었습니다.";
                            JOptionPane.showMessageDialog(null, contentText, "SUCCESS_MESSAGE", JOptionPane.PLAIN_MESSAGE);
                        }else if (command.getResponseType() == ResponseType.WRONG_PASSWORD){
                            contentText = "비밀번호가 틀렸습니다.";
                            JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
                        }else if (command.getResponseType() == ResponseType.FAILURE){
                            contentText = "error";
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