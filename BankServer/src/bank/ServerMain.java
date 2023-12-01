package bank;

import common.AccountType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;



//*******************************************************************
// # 51
//*******************************************************************
// Name : ServerMain
// Type : Class
// Description :  BankServer�� GUI �������̸�, ATM���� ��������� ����Ѵ�.
//                ���� �������� �����ϰ� ������, ���� ��ɵ��� ������ �ִ�.
//*******************************************************************

class ServerMain
        extends JFrame
        implements ActionListener, ClientHandler {
    private JLabel Label_UserCount;
    private JLabel Label_UserCount_2;
    private JToggleButton Btn_StartStop;
    private JButton Btn_Reset;
    private JTextArea TextArea_Log;
    private JScrollPane sp;


    private static AsynchronousChannelGroup channelGroup;
    private static AsynchronousServerSocketChannel serverSocketChannel;
    private List<CustomerVO> customerList;
    private List<Client> clientList = new Vector<>();


    //*******************************************************************
    // # 51-01
    //*******************************************************************
    // Name : ServerMain()
    // Type : ������
    // Description :  ServerMain Class�� �����ڷμ� ���� ������ Load �ϰ�, GUI�� �ʱ�ȭ �Ѵ�.
    //                ���� ������ ./Account.txt�� �����ϸ� Server ����� Load, ����� Save ������ �Ѵ�
    //*******************************************************************
    public ServerMain()
    {

        InitGui();
        customerList = ReadCustomerFile("./Account.txt");
        setVisible(true);

        // WindowListener �߰�
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // �������� ����� �� SaveCustomerFile �޼��� ȣ��
                SaveCustomerFile(customerList, "./Account.txt");
            }
        });

    }

    //*******************************************************************
    // # 51-01-01
    //*******************************************************************
    // Name : GetDefaultCustomers()
    // Type : Method
    // Description :  Server ���� �� ����� ���� ������ ������ Default ���¸� �����ϴ� ���
    //*******************************************************************
    private static List<CustomerVO> GetDefaultCustomers() {
        List<CustomerVO> customerList = new Vector<>();
        customerList.add(new CustomerVO("202300001","����","202300001", new AccountVO("����", "202300001", AccountType.CHECKING, 100_000_000, Date.valueOf(LocalDate.now()))));
        customerList.add(new CustomerVO("202300002","��ö","202300002", new AccountVO("��ö", "202300002", AccountType.CHECKING, 10_000_000, Date.valueOf(LocalDate.now()))));
        customerList.add(new CustomerVO("202300003","����","202300003", new AccountVO("����", "202300003", AccountType.CHECKING, 5_000_000, Date.valueOf(LocalDate.now()))));
        customerList.add(new CustomerVO("202300004","����","202300004", new AccountVO("����", "202300004", AccountType.CHECKING, 1_000_000, Date.valueOf(LocalDate.now()))));
        return customerList;
    }


    //*******************************************************************
    // # 51-01-02
    //*******************************************************************
    // Name : SaveCustomerFile()
    // Type : Method
    // Description :  ��������� ���� ������ txt ���Ϸ� �����ϴ� ���
    //*******************************************************************
    public void SaveCustomerFile(List<CustomerVO> customers, String filePath)
    {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath)))
        {
            oos.writeObject(customers);
            System.out.println("Objects saved to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //*******************************************************************
    // # 51-01-02
    //*******************************************************************
    // Name : SaveCustomerFile()
    // Type : Method
    // Description :  txt ���Ϸ� ����� ���� ������ Load �ϴ� ���
    //*******************************************************************
    public List<CustomerVO> ReadCustomerFile(String filePath)
    {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./Account.txt")))
        {
            List<CustomerVO> customers = (List<CustomerVO>) ois.readObject();
            System.out.println("Objects read from " + filePath);
            return customers;
        }
        catch (IOException | ClassNotFoundException e)
        {
            // ������ �������� ���� �� �ʱ� �����ͷ� ����Ʈ�� �ʱ�ȭ
            System.out.println("File not found. Initializing with default data.");
            List<CustomerVO> defaultCustomers = GetDefaultCustomers();
            SaveCustomerFile(defaultCustomers, filePath);
            return defaultCustomers;
        }
    }

    //*******************************************************************
    // # 51-02 (GUI)
    //*******************************************************************
    // Name : InitGui
    // Type : Method
    // Description :  ServerMain Class�� GUI ������Ʈ�� �Ҵ��ϰ� �ʱ�ȭ �Ѵ�.
    //                ServerMain Frame�� ���� ���� ��ư �� �ؽ�Ʈ â �ʱ�ȭ ��ư�� ������ �ִ�
    //*******************************************************************
    private void InitGui()
    {
        setTitle("���� GUI");
        setSize(480, 320);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        Label_UserCount = new JLabel("���� ���� ��: ");
        topPanel.add(Label_UserCount);

        Label_UserCount_2 = new JLabel("0");
        topPanel.add(Label_UserCount_2);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        TextArea_Log = new JTextArea();
        TextArea_Log.setEditable(false);
        sp = new JScrollPane(TextArea_Log);
        mainPanel.add(sp, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        Btn_StartStop = new JToggleButton("����");
        Btn_StartStop.addActionListener(this);
        bottomPanel.add(Btn_StartStop);

        Btn_Reset = new JButton("�ؽ�Ʈ â �ʱ�ȭ");
        Btn_Reset.addActionListener(this);
        bottomPanel.add(Btn_Reset);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setLocationRelativeTo(null);
        setVisible(true);

    }

    //*******************************************************************
    // # 51-02-01
    //*******************************************************************
    // Name : actionPerformed
    // Type : Listener
    // Description :  ServerMain Frame�� ��ư ������Ʈ���� ������ ������ �κ�
    //                �Ʒ� �ڵ忡���� ���� Start/Stop ��� ��ư ��� �� �ؽ�Ʈâ �ʱ�ȭ ��ư����� ���� �Ǿ� �ִ�.
    //*******************************************************************
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == Btn_StartStop)
        {
            if (Btn_StartStop.isSelected())
            {
                startServer();
            } else
            {
                stopServer();
            }
        }

        else if (e.getSource() == Btn_Reset)
        {
           // TextArea_Log.removeAll();
            TextArea_Log.setText(null);
        }
    }




    //*******************************************************************
    // # 51-03-01
    //*******************************************************************
    // Name : startServer
    // Type : Method
    // Description :  ���� ������ port 5001 �� bind �Ͽ� open �ϴ� ��� ��
    //                Ŭ���̾�Ʈ ������ ���� �õ��� accept �Ͽ� ���� ��Ű�� ����� ���� �Ǿ� �ִ�.
    //*******************************************************************
    public void startServer()
    {
        try
        {
            channelGroup = AsynchronousChannelGroup.withFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
            serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
            serverSocketChannel.bind(new InetSocketAddress(5001));
            SwingUtilities.invokeLater(() ->
            {
                addMsg("���� ����");
                Btn_StartStop.setText("����");
            });
            serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>()
            {
                @Override
                public void completed(AsynchronousSocketChannel socketChannel, Void attachment)
                {
                    try
                    {
                        addMsg(socketChannel.getRemoteAddress() + " ����");
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    Client client = new Client(socketChannel, ServerMain.this, customerList);
                    clientList.add(client);
                    // ���� �����ڼ� ������Ʈ
                    SwingUtilities.invokeLater(() -> Label_UserCount_2.setText(String.valueOf(clientList.size())));
                    serverSocketChannel.accept(null, this);
                }

                @Override
                public void failed(Throwable exc, Void attachment)
                {
                    if (serverSocketChannel.isOpen())
                    {
                        stopServer();
                    }
                }
            });
        }
        catch (IOException e)
        {
            if (serverSocketChannel.isOpen())
            {
                stopServer();
            }
        }
    }

    //*******************************************************************
    // # 51-03-02
    //*******************************************************************
    // Name : stopServer
    // Type : Method
    // Description :  ���� ������ ���� ���� �ϴ� ���
    //*******************************************************************
    public void stopServer()
    {
        clientList.clear();
        if (channelGroup != null && !channelGroup.isShutdown())
        {
            try
            {
                channelGroup.shutdownNow();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                SwingUtilities.invokeLater(() ->
                {
                    addMsg("���� ����");
                    Btn_StartStop.setText("����");
                });
            }
        }
    }


    //*******************************************************************
    // # 51-03-03
    //*******************************************************************
    // Name : removeClient()
    // Type : Method
    // Description :  Ŭ���̾�Ʈ ������ ���� �Ǿ��� ��
    //                ServerMain �� clientList ����Ʈ ���� �ش� �ε����� �����ϴ� ���
    //*******************************************************************
    @Override
    public void removeClient(Client client)
    {
        clientList.remove(client);
        addMsg(client + "���ŵ�");
        // ���� �����ڼ� ������Ʈ
        SwingUtilities.invokeLater(() -> Label_UserCount_2.setText(String.valueOf(clientList.size())));
    }

    @Override
    public void displayInfo(String msg)
    {
        addMsg(msg);
    }
    public void addMsg(String data)
    {
        TextArea_Log.append(data + "\n");
    }

    public static void main(String[] args) throws Exception
    {
        ServerMain f = new ServerMain();
    }
}

