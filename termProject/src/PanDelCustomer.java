import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanDelCustomer  extends JPanel implements ActionListener
{
    private JLabel Label_CustomerName, Label_ID, Label_PassWord, Label_PhoneNum;
    private  JTextField Text_CustomerName, Text_ID, Text_PassWord, Text_PhoneNum;

    private JButton Btn_Del;
    private JButton Btn_Close;

    ManagerMain MainFrame;
    public PanDelCustomer(ManagerMain parent)
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

        Label_ID = new JLabel("아이디");
        Label_ID.setBounds(0,90,100,20);
        Label_ID.setHorizontalAlignment(JLabel.LEFT);
        add(Label_ID);

        Text_ID = new JTextField();
        Text_ID.setBounds(100,90,350,20);
        Text_ID.setEditable(true);
        add(Text_ID);

        Label_PassWord = new JLabel("비밀 번호");
        Label_PassWord.setBounds(0,130,100,20);
        Label_PassWord.setHorizontalAlignment(JLabel.LEFT);
        add(Label_PassWord);

        Text_PassWord = new JTextField();
        Text_PassWord.setBounds(100,130,350,20);
        Text_PassWord.setEditable(true);
        add(Text_PassWord);

        Label_PhoneNum = new JLabel("전화 번호");
        Label_PhoneNum.setBounds(0,170,100,20);
        Label_PhoneNum.setHorizontalAlignment(JLabel.LEFT);
        add(Label_PhoneNum);

        Text_PhoneNum = new JTextField();
        Text_PhoneNum.setBounds(100,170,350,20);
        Text_PhoneNum.setEditable(true);
        add(Text_PhoneNum);


        Btn_Del = new JButton("삭제");
        Btn_Del.setBounds(140,250,70,20);
        Btn_Del.addActionListener(this);
        add(Btn_Del);

        Btn_Close = new JButton("취소");
        Btn_Close.setBounds(250,250,70,20);
        Btn_Close.addActionListener(this);
        add(Btn_Close);
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Btn_Close) {
            this.setVisible(false);
            MainFrame.display("Main");
        }
    }
}
