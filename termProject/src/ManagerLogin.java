import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManagerLogin   extends JPanel implements ActionListener
{
    private JLabel Label_ID, Label_PassWord;
    private  JTextField Text_ID, Text_PassWord;

    private JButton Btn_View;
    private JButton Btn_Close;

    ManagerMain MainFrame;
    public ManagerLogin(ManagerMain parent)
    {
        MainFrame = parent;
        InitGUI();
    }

    private void InitGUI()
    {
        setLayout(null);
        setBounds(0,0,480,320);

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

        Btn_View = new JButton("로그인");
        Btn_View.setBounds(140,250,70,20);
        Btn_View.addActionListener(this);
        add(Btn_View);

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
