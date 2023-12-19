
import common.*;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;


import common.*;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;

public class ManagerLogin   extends JPanel implements ActionListener {
    private JLabel Label_ID, Label_PassWord;
    private JTextField Text_ID, Text_PassWord;

    private JButton Btn_Transfer;
    private JButton Btn_Close;

    ManagerMain MainFrame;

    public ManagerLogin(ManagerMain parent) {
        MainFrame = parent;
        InitGUI();
    }

    private void InitGUI() {
        setLayout(null);
        setBounds(0, 0, 480, 320);

        Label_ID = new JLabel("아이디");
        Label_ID.setBounds(0, 90, 100, 20);
        Label_ID.setHorizontalAlignment(JLabel.LEFT);
        add(Label_ID);

        Text_ID = new JTextField();
        Text_ID.setBounds(100, 90, 350, 20);
        Text_ID.setEditable(true);
        add(Text_ID);

        Label_PassWord = new JLabel("비밀 번호");
        Label_PassWord.setBounds(0, 130, 100, 20);
        Label_PassWord.setHorizontalAlignment(JLabel.LEFT);
        add(Label_PassWord);

        Text_PassWord = new JTextField();
        Text_PassWord.setBounds(100, 130, 350, 20);
        Text_PassWord.setEditable(true);
        add(Text_PassWord);

        Btn_Transfer = new JButton("로그인");
        Btn_Transfer.setBounds(140, 250, 70, 20);
        Btn_Transfer.addActionListener(this);
        add(Btn_Transfer);

        Btn_Close = new JButton("취소");
        Btn_Close.setBounds(250, 250, 70, 20);
        Btn_Close.addActionListener(this);
        add(Btn_Close);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Btn_Close) {
            this.setVisible(false);
            MainFrame.display("Main");
        }
        if (e.getSource() == Btn_Transfer) {
            Login();
        }
    }

    public void Login() {
        String enteredManagername = Text_ID.getText();
        String enteredPassword = Text_PassWord.getText();

        MainFrame.send(new CommandDTO(RequestType.MANAGERLOGIN, enteredManagername, enteredPassword), new CompletionHandler<Integer, ByteBuffer>() {
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
                        String contentText = null;

                        if (command.getResponseType() == ResponseType.SUCCESS) {
                            contentText = "로그인되었습니다.";
                            JOptionPane.showMessageDialog(null, contentText, "SUCCESS_MESSAGE", JOptionPane.PLAIN_MESSAGE);
                            setVisible(false);
                            MainFrame.display("Main");
                        } else if (command.getResponseType() == ResponseType.FAILURE) {
                            contentText = "아이디 또는 비밀번호가 일치하지 않습니다.";
                            JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
                        } else {
                            contentText = "ERROR.";
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


    /*
    Map<String, String> ManagerCredentials = readManagerCredentials("./ManagerID.txt");

        if(

    validateCredentials(ManagerCredentials, enteredManagername, enteredPassword))

    {
        JOptionPane.showMessageDialog(this, "로그인 성공!");
        this.setVisible(false);
        MainFrame.display("Main");
    } else

    {
        JOptionPane.showMessageDialog(this, "로그인 실패. 아이디 또는 비밀번호를 확인하세요.");
    }*/
    }

    /*private Map<String, String> readManagerCredentials(String filePath) {
        Map<String, String> userCredentials = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    userCredentials.put(username, password);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userCredentials;
    }

    private boolean validateCredentials(Map<String, String> userCredentials, String username, String password) {
        return userCredentials.containsKey(username) && userCredentials.get(username).equals(password);
    }*/

}
