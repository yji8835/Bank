import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanDelAccount extends JPanel implements ActionListener
{
    private JLabel Label_CustomerName, Label_Account, Label_PassWord;
    private  JTextField Text_CustomerName, Text_Account, Text_PassWord;

    private JButton Btn_Del;
    private JButton Btn_Close;

    ManagerMain MainFrame;
    public PanDelAccount(ManagerMain parent)
    {
        MainFrame = parent;
        InitGUI();
    }

    private void InitGUI()
    {
        setLayout(null);
        setBounds(0,0,480,320);

        Label_CustomerName = new JLabel("고객 이름");
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

        Label_PassWord = new JLabel("계좌 암호");
        Label_PassWord.setBounds(0,150,100,20);
        Label_PassWord.setHorizontalAlignment(JLabel.LEFT);
        add(Label_PassWord);

        Text_PassWord = new JTextField();
        Text_PassWord.setBounds(100,150,350,20);
        Text_PassWord.setEditable(true);
        add(Text_PassWord);

        Btn_Del = new JButton("삭제");
        Btn_Del.setBounds(140,250,70,20);
        Btn_Del.addActionListener(this);
        add(Btn_Del);

        Btn_Close = new JButton("닫기");
        Btn_Close.setBounds(250,250,70,20);
        Btn_Close.addActionListener(this);
        add(Btn_Close);
    }
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == Btn_Close)
        {
            this.setVisible(false);
            MainFrame.display("Main");
        }
        /*if (e.getSource() == Btn_Del)
        {
            View();
        }*/
    }

}