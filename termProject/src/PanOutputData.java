import bank.AccountVO;
import bank.CustomerVO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PanOutputData extends JFrame implements ActionListener {
    ManagerMain MainFrame;
    JTable table;
    JScrollPane scroll;
    List<AccountVO> AccountList;
    String[][] data;
    String[] title = {"고객명", "계좌번호", "계좌타입", "잔액", "개설일"};

    public PanOutputData(ManagerMain parent) {
        MainFrame = parent;
        InitGUI();
    }

    public PanOutputData() {
        InitGUI();

    }

    public List<AccountVO> ReadData(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            List<CustomerVO> rawData = (List<CustomerVO>) ois.readObject();

            List<AccountVO> accountList = new ArrayList<>();
            for (int i = 0; i < rawData.size(); i++) {
                ArrayList<AccountVO> list = rawData.get(i).getAccountlist();
                for (int j = 0; j < list.size(); j++) {
                    accountList.add(list.get(j));
                }
            }
            System.out.println("Objects read from " + filePath);
            return accountList;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("File not found or class cast error. Initializing with default data.");
            return Collections.emptyList();
        }
    }

    private void InitGUI() {
        AccountList = ReadData("./Account.txt");
        if (AccountList != null) {
            AccountList.sort(Comparator.comparing(account -> {
                String owner = account.getOwner();
                return (owner != null) ? owner : "";
            }));
            int accountNum = AccountList.size();
            data = new String[accountNum][5];

            for (int i = 0; i < accountNum; i++) {
                AccountVO account = AccountList.get(i);
                data[i][0] = account.getOwner();
                data[i][1] = account.getAccountNo();
                data[i][2] = String.valueOf(account.getType());
                data[i][3] = String.valueOf(account.getBalance());
                data[i][4] = String.valueOf(account.getOpenDate());
            }
        }
        table = new JTable(data, title);
        scroll = new JScrollPane(table);
        add(scroll);
        pack();
        setVisible(true);
        setSize(420,300);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
