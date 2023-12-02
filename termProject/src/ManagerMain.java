import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ManagerMain extends JFrame implements ActionListener {
    private JLabel Label_Title;
    private JButton Btn_NewAccount;
    private JButton Btn_ViewAccount;
    private JButton Btn_DelAccount;
    private JButton Btn_NewCustomer;
    private JButton Btn_ViewCustomer;
    private JButton Btn_DelCustomer;
    private JButton Btn_OutputData;
    private JButton Btn_Exit;


    PanViewAccount Pan_ViewAccount;
    PanNewAccount Pan_NewAccount;
    PanDelAccount Pan_DelAccount;
    PanNewCustomer Pan_NewCustomer;
    PanViewCustomer Pan_ViewCustomer;
    PanDelCustomer Pan_DelCustomer;
    PanOutputData Pan_OutputData;

    
    public ManagerMain() {
//        starClient();
        InitGui();
        setVisible(true);
    }
    private void InitGui() {
        setLayout(null);

        setTitle("Manager GUI");
        setBounds(0, 0, 480, 320);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        


        Label_Title = new JLabel("Bank Manager");
        Label_Title.setFont(new Font("Arial", Font.PLAIN, 30));
        Label_Title.setSize(getWidth(), 60);
        Label_Title.setLocation(0, 0);
        Label_Title.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Title);

        Btn_NewAccount = new JButton("계좌 추가");
        Btn_NewAccount.setSize(100,70);
        Btn_NewAccount.setLocation(20, 60);
        Btn_NewAccount.addActionListener(this);
        add(Btn_NewAccount);

        Btn_ViewAccount = new JButton("계좌 조회");
        Btn_ViewAccount.setSize(100,70);
        Btn_ViewAccount.setLocation(150, 60);
        Btn_ViewAccount.addActionListener(this);
        add(Btn_ViewAccount);

        Btn_DelAccount = new JButton("계좌 삭제");
        Btn_DelAccount.setSize(100,70);
        Btn_DelAccount.setLocation(280, 60);
        Btn_DelAccount.addActionListener(this);
        add(Btn_DelAccount);

        Btn_NewCustomer = new JButton("신규 고객");
        Btn_NewCustomer.setSize(100,70);
        Btn_NewCustomer.setLocation(20, 130);
        Btn_NewCustomer.addActionListener(this);
        add(Btn_NewCustomer);

        Btn_ViewCustomer = new JButton("고객 열람");
        Btn_ViewCustomer.setSize(100,70);
        Btn_ViewCustomer.setLocation(150, 130);
        Btn_ViewCustomer.addActionListener(this);
        add(Btn_ViewCustomer);

        Btn_DelCustomer = new JButton("고객 삭제");
        Btn_DelCustomer.setSize(100,70);
        Btn_DelCustomer.setLocation(280, 130);
        Btn_DelCustomer.addActionListener(this);
        add(Btn_DelCustomer);

        Btn_OutputData = new JButton("데이터 출력");
        Btn_OutputData.setSize(230,70);
        Btn_OutputData.setLocation(20, 200);
        Btn_OutputData.addActionListener(this);
        add(Btn_OutputData);

        Btn_Exit = new JButton("종료");
        Btn_Exit.setSize(100,70);
        Btn_Exit.setLocation(280, 200);
        Btn_Exit.addActionListener(this);
        add(Btn_Exit);

        Pan_NewAccount = new PanNewAccount(this);
        add(Pan_NewAccount);
        Pan_NewAccount.setVisible(false);
        
        Pan_ViewAccount = new PanViewAccount(this);
        add(Pan_ViewAccount);
        Pan_ViewAccount.setVisible(false);

        Pan_DelAccount = new PanDelAccount(this);
        add(Pan_DelAccount);
        Pan_DelAccount.setVisible(false);

        Pan_NewCustomer = new PanNewCustomer(this);
        add(Pan_NewCustomer);
        Pan_NewCustomer.setVisible(false);

        Pan_ViewCustomer = new PanViewCustomer(this);
        add(Pan_ViewCustomer);
        Pan_ViewCustomer.setVisible(false);

        Pan_DelCustomer = new PanDelCustomer(this);
        add(Pan_DelCustomer);
        Pan_DelCustomer.setVisible(false);

        /*Pan_OutputData = new PanOutputData(this);
        add(Pan_OutputData);
        Pan_OutputData.setVisible(false);*/
    }
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == Btn_NewAccount)
        {
            display("NewAccount");
        }
        else if (e.getSource() == Btn_ViewAccount)
        {
            display("ViewAccount");
        }
        else if (e.getSource() == Btn_DelAccount)
        {
            display("DelAccount");
        }
        else if (e.getSource() == Btn_NewCustomer)
        {
            display("NewCustomer");
        }
        else if (e.getSource() == Btn_ViewCustomer)
        {
            display("ViewCustomer");
        }
        else if (e.getSource() == Btn_DelCustomer)
        {
            display("DelCustomer");
        }
        else if (e.getSource() == Btn_OutputData)
        {
            display("OutputData");
        }
        else if (e.getSource() == Btn_Exit)
        {
            dispose();
        }
    }
    public void display(String viewName) {

        if(viewName.equals("ViewAccount") == true)
        {
            SetFrameUI(false);
            Pan_ViewAccount.setVisible(true);
        }
        if(viewName.equals("NewAccount") == true)
        {
            SetFrameUI(false);
            Pan_NewAccount.setVisible(true);
        }
        if(viewName.equals("DelAccount") == true)
        {
            SetFrameUI(false);
            Pan_DelAccount.setVisible(true);
        }
        if(viewName.equals("ViewCustomer") == true)
        {
            SetFrameUI(false);
            Pan_ViewCustomer.setVisible(true);
        }
        if(viewName.equals("DelCustomer") == true)
        {
            SetFrameUI(false);
            Pan_DelCustomer.setVisible(true);
        }
        if(viewName.equals("NewCustomer") == true)
        {
            SetFrameUI(false);
            Pan_NewCustomer.setVisible(true);
        }
        if(viewName.equals("Main") == true)
        {
            SetFrameUI(true);
        }

    }

    void SetFrameUI(Boolean bOn)
    {
        if(bOn == true)
        {
            Label_Title.setVisible(true);
            Btn_NewAccount.setVisible(true);
            Btn_ViewAccount.setVisible(true);
            Btn_DelAccount.setVisible(true);
            Btn_NewCustomer.setVisible(true);
            Btn_ViewCustomer.setVisible(true);
            Btn_DelCustomer.setVisible(true);
            Btn_OutputData.setVisible(true);
            Btn_Exit.setVisible(true);
        }
        else {

            Label_Title.setVisible(false);
            Btn_NewAccount.setVisible(false);
            Btn_ViewAccount.setVisible(false);
            Btn_DelAccount.setVisible(false);
            Btn_NewCustomer.setVisible(false);
            Btn_ViewCustomer.setVisible(false);
            Btn_DelCustomer.setVisible(false);
            Btn_OutputData.setVisible(false);
            Btn_Exit.setVisible(false);
        }
    }


    public static void main(String[] args) throws Exception
    {
        ManagerMain my = new ManagerMain();
    }
}

