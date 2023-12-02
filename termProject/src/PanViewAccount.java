import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanViewAccount extends JPanel implements ActionListener
{
    private JLabel Label_CustomerName, Label_Account, Label_balance;
    private  JTextField Text_CustomerName, Text_Account;
    private  JTextArea Text_balance;

    private JButton Btn_View;
    private JButton Btn_Close;

    ManagerMain MainFrame;
    public PanViewAccount(ManagerMain parent)
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

        Label_balance = new JLabel("잔액");
        Label_balance.setBounds(0,150,100,20);
        Label_balance.setHorizontalAlignment(JLabel.LEFT);
        add(Label_balance);

        Text_balance = new JTextArea();
        Text_balance.setBounds(100,150,350,20);
        Text_balance.setEditable(false);
        add(Text_balance);

        Btn_View = new JButton("조회");
        Btn_View.setBounds(140,250,70,20);
        Btn_View.addActionListener(this);
        add(Btn_View);

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
        /*if (e.getSource() == Btn_View)
        {
            View();
        }*/
    }

}
