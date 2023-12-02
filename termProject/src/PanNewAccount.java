import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanNewAccount extends JPanel implements ActionListener {
    private JPanel AccountType;
    private JLabel Label_CustomerName;
    private JTextField Text_CustomerName;
    private JLabel Label_Account;
    private JTextField Text_Account;
    private JLabel Label_type;
    private JRadioButton Checking, Saving;
    private ButtonGroup TypeGroup;
    private JButton Btn_View;
    private JButton Btn_Close;

    ManagerMain MainFrame;

    public PanNewAccount(ManagerMain parent) {
        MainFrame = parent;
        InitGUI();
    }

    private void InitGUI() {
        setLayout(null);
        setBounds(0, 0, 480, 320);

        Label_CustomerName = new JLabel("고객 이름");
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

        Label_type = new JLabel("계좌 타입");
        Label_type.setBounds(0, 150, 100, 20);
        Label_type.setHorizontalAlignment(JLabel.LEFT);
        add(Label_type);

        AccountType = new JPanel();
        AccountType.setBounds(100, 150, 350, 40);
        Checking = new JRadioButton("당좌");
        Checking.addActionListener(this);
        Saving = new JRadioButton("저축");
        Saving.addActionListener(this);
        TypeGroup = new ButtonGroup();
        TypeGroup.add(Checking);
        TypeGroup.add(Saving);
        AccountType.add(Checking);
        AccountType.add(Saving);
        Checking.doClick();
        add(AccountType);


        Btn_View = new JButton("등록");
        Btn_View.setBounds(140, 250, 70, 20);
        Btn_View.addActionListener(this);
        add(Btn_View);

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
    }
}
