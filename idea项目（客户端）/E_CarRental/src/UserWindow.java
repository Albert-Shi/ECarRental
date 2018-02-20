import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;

/**
 * Created by 史书恒 on 2016/11/12.
 */
public class UserWindow extends JFrame {
    JFrame father;
    UsersInfo user;
    int select;

    CarsInfo[] carsInfos;
    ArrayList<RentInfo> PriRentInfos;

    UserWindow(JFrame father, JSONObject userJSON) {
        this.father = father;
        this.user = new UsersInfo();
        try{//获取当前用户信息，并存入 user
            user.name = userJSON.getString("name");
            user.account = userJSON.getString("account");
            user.admin = userJSON.getString("admin");
            user.password = userJSON.getString("password");
            user.cash = userJSON.getString("cash");
        } catch (Exception e) {
            e.printStackTrace();
        }
        father.setVisible(false);
        init();
    }

    void init() {//创建窗口
        try {
            Box baseBox, infoBox, carsBox, groupBox, buttonBox;
            JScrollPane carscroller, rentscroller;
            DefaultListModel carsListModel, rentListModel;
            JList<CarsInfo> carsInfoJList, rentInfoJlist;
            JButton rentButton, returnButton, modifyButton, prepaidButton, refreshButton, exitButton;

            infoBox = Box.createVerticalBox();
            carsBox = Box.createVerticalBox();
            groupBox = Box.createHorizontalBox();
            buttonBox = Box.createHorizontalBox();
            baseBox = Box.createVerticalBox();
            rentButton = new JButton("租车");
            returnButton = new JButton("还车");
            prepaidButton = new JButton("充值");
            modifyButton = new JButton("修改用户信息");
            refreshButton = new JButton("刷新信息");
            exitButton = new JButton("退出");
            groupBox.add(Box.createVerticalStrut(5));
            groupBox.add(infoBox);
            groupBox.add(Box.createVerticalStrut(5));
            groupBox.add(carsBox);
            groupBox.add(Box.createVerticalStrut(5));
            buttonBox.add(rentButton);
            buttonBox.add(Box.createHorizontalStrut(10));
            buttonBox.add(returnButton);
            buttonBox.add(Box.createHorizontalStrut(10));
            buttonBox.add(prepaidButton);
            buttonBox.add(Box.createHorizontalStrut(10));
            buttonBox.add(modifyButton);
            buttonBox.add(Box.createHorizontalStrut(10));
            buttonBox.add(refreshButton);
            buttonBox.add(Box.createHorizontalStrut(10));
            buttonBox.add(exitButton);
            baseBox.add(groupBox);
            baseBox.add(Box.createHorizontalStrut(10));
            baseBox.add(buttonBox);

            carsListModel = new DefaultListModel();
            rentListModel = new DefaultListModel();

            dealJSON(carsListModel, rentListModel);

            carsInfoJList = new JList<>(carsListModel);
            carsInfoJList.addMouseListener(new MoreInfo(carsInfos));
            carscroller = new JScrollPane(carsInfoJList);
            rentInfoJlist = new JList<>(rentListModel);
            rentscroller = new JScrollPane(rentInfoJlist);

            JLabel mycash = new JLabel("余额/元："+ user.cash);
            ButtonActions rentAction = new ButtonActions(0, carsInfos, user, carsInfoJList, mycash, this);
            ButtonActions backAction = new ButtonActions(PriRentInfos, user, rentscroller, rentInfoJlist, mycash, this);
            rentButton.addActionListener(rentAction);
            returnButton.addActionListener(backAction);
            prepaidButton.addActionListener(new ButtonActions(3, carsInfos, user, carsInfoJList, mycash, this));
            modifyButton.addActionListener(new ButtonActions(2, carsInfos, user, carsInfoJList, mycash, this));
            refreshButton.addActionListener(new ButtonActions(4, carsInfos, user, carsInfoJList, mycash, this));
            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new Login();
                    dispose();
                }
            });


            JLabel acc = new JLabel("账户：" + user.account);
            JLabel name = new JLabel("姓名：" + user.name);
            JLabel ad = new JLabel("账户类型：非管理员账户");
            infoBox.add(new JLabel("账户信息"));
            infoBox.add(Box.createHorizontalStrut(2));
            infoBox.add(acc);
//            infoBox.add(Box.createHorizontalStrut(2));
            infoBox.add(name);
//            infoBox.add(Box.createHorizontalStrut(2));
            infoBox.add(ad);
//            infoBox.add(Box.createHorizontalStrut(2));
            infoBox.add(name);
//            infoBox.add(Box.createHorizontalStrut(2));
            infoBox.add(mycash);
            infoBox.add(Box.createHorizontalStrut(20));
            add(baseBox);

            carsBox.add(new JLabel("可租车辆信息"));
//            carsBox.add(Box.createHorizontalStrut(1));
            carsBox.add(carscroller);
            setSize(550, 300);
            setTitle(user.account);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setVisible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    void dealJSON(DefaultListModel carsListMode, DefaultListModel hasRentListMode) {//处理JSON
//        String jsonuser = Server.getString(Server.HOST + "/php?admin=true&getinfomode=5");
        String jsoncar = Server.getString(Server.HOST + "/php?admin=true&getinfomode=6");
        String jsonrent = Server.getString(Server.HOST + "/php?getinfomode=8");
        try {
            JSONObject root = new JSONObject(jsoncar);
            JSONObject data = root.getJSONObject("data");
            JSONArray cars = data.getJSONArray("carinfo");
            carsInfos = null;
            carsInfos = new CarsInfo[cars.length()];
            int ci = 0;
            for (int i = 0; i < cars.length(); i++) {
                JSONObject car = cars.getJSONObject(i);
                if (car.getString("able").equals("1")) {
                    carsInfos[ci] = new CarsInfo();
                    carsInfos[ci].able = car.getString("able");
                    carsInfos[ci].brand = car.getString("brand");
                    carsInfos[ci].cargo = car.getString("cargo");
                    carsInfos[ci].people = car.getString("people");
                    carsInfos[ci].name = car.getString("name");
                    carsInfos[ci].number = car.getString("number");
                    carsInfos[ci].price = car.getString("price");
                    carsInfos[ci].style = car.getString("style");
                    carsInfos[ci].deposit = car.getString("deposit");
                    carsInfos[ci].distance = car.getString("distance");
                    carsInfos[ci].note = car.getString("note");
                    carsInfos[ci].picture = car.getString("picture");
                    carsListMode.addElement(carsInfos[ci].number);
                    ci++;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONObject all = new JSONObject(jsonrent);
            JSONArray data = all.getJSONArray("data");
            PriRentInfos = null;
            PriRentInfos = new ArrayList<>();
            hasRentListMode.removeAllElements();
            for (int i = 0; i < data.length(); i++) {
                JSONObject d = data.getJSONObject(i);
                if (d.getString("account").equals(user.account)) {
                    RentInfo info = new RentInfo();
                    info.account = d.getString("account");
                    info.day = d.getString("day");
                    info.number = d.getString("number");
                    info.startdate = d.getString("startdate");
                    info.total = d.getString("total");
                    PriRentInfos.add(info);
                    hasRentListMode.addElement(info.number + "\t已租："+info.day+"\t共计租："+info.total+"天");
                }
            }

        }catch (Exception re) {
            re.printStackTrace();
        }
    }
}

class ButtonActions implements ActionListener {//按键监听器
    int select;
    CarsInfo[] carsInfos;
    UsersInfo user;
    ArrayList rentinfos;
    JList list;
    JLabel mycash;
    JFrame father;
    JScrollPane scrollPane;
    ButtonActions(int select, CarsInfo[] carsInfos, UsersInfo user, JList list, JLabel cash, JFrame father) {
        this.select = select;
        this.carsInfos = carsInfos;
        this.user = user;
        this.list = list;
        this.mycash = cash;
        this.father = father;
    }
    ButtonActions(ArrayList rentinfos, UsersInfo user, JScrollPane scroller, JList rentlist, JLabel cash, JFrame father) {
        this.rentinfos = rentinfos;
        this.user = user;
        this.list = rentlist;
        this.mycash = cash;
        this.scrollPane = scroller;
        this.father = father;
        this.select = 1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (select == 0) {//租车按钮
            try {
                int price = Integer.parseInt(carsInfos[list.getSelectedIndex()].price);
                int deposit = Integer.parseInt(carsInfos[list.getSelectedIndex()].deposit);
                int cash = Integer.parseInt(user.cash);

                JDialog dialog = new JDialog();
                JTextField totalEditor = new JTextField();
                JButton positiveButton = new JButton("确定");
                dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                dialog.setTitle("提示");
                Box layout = Box.createVerticalBox();
                layout.add(new JLabel("请输入租赁天数:"));
                layout.add(totalEditor);
                Box buttonBox = Box.createHorizontalBox();
                buttonBox.add(Box.createHorizontalStrut(50));
                buttonBox.add(positiveButton);
                layout.add(buttonBox);
                positiveButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Dialog cashDialog = new JDialog();
                        cashDialog.setTitle("提示");
                        cashDialog.setSize(300, 120);
                        cashDialog.setLocationRelativeTo(null);
                        try {
                            int rentCash = price * Integer.parseInt(totalEditor.getText());
                            if (rentCash > cash) {
                                cashDialog.add(new JLabel("余额不足，无法租车!"));
                            }
                            else {
                                String info = Server.getString(Server.HOST+"/php/?deal=1&account="+user.account+"&number="+carsInfos[list.getSelectedIndex()].number+"&total="+totalEditor.getText());
                                if (info.equals("successful")) {
                                    cashDialog.add(new JLabel("已成功租车并支付保证金"+deposit+"元!请关闭对话框"));
//                                    totalEditor.setText("已成功租车!请关闭对话框");
                                    refresh();
                                }
                                else
                                    cashDialog.add(new JLabel("租车失败!请重试"));
//                                    totalEditor.setText("租车失败!");
                            }
                        }catch (Exception exp) {
                            cashDialog.add(new JLabel("租车失败!请重试"));
//                            totalEditor.setText("租车失败,请重新输入或关闭对话框!");
                        }finally {
                            cashDialog.setVisible(true);
                            dialog.dispose();
                        }
                    }
                });
                dialog.add(layout);
                dialog.setAlwaysOnTop(true);
                dialog.setSize(200,100);
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            } catch (Exception e0) {
                JDialog d = new JDialog();
                d.setSize(160, 80);
                d.setTitle("错误");
                d.add(new JLabel("请先选择要租的车辆!"));
                d.setLocationRelativeTo(null);
                d.setVisible(true);
            }
        }
        if (select == 1) {//还车按钮
            try {
                JDialog selectionDialog = new JDialog();
                Box layout = Box.createVerticalBox();
                Box buttonGroup = Box.createHorizontalBox();
                layout.add(scrollPane);
                layout.add(buttonGroup);
                JButton b = new JButton("确定");
                b.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int index = list.getSelectedIndex();
                        if (index >= 0) {
                            try {
                                selectionDialog.dispose();
                                RentInfo rentInfo = (RentInfo)rentinfos.get(index);
                                String number = rentInfo.number;
                                String json = Server.getString(Server.HOST + "/php/?getinfomode=2&number=" + number);
                                JSONObject all = new JSONObject(json);
                                JSONObject data = all.getJSONObject("data");
                                JSONArray cars = data.getJSONArray("carinfo");
                                JSONObject car = cars.getJSONObject(0);
                                int price = Integer.parseInt(car.getString("price"));

                                int cash = Integer.parseInt(user.cash);
                                int total = Integer.parseInt(rentInfo.total);
                                int day = Integer.parseInt(rentInfo.day);

                                JDialog tipsdialog = new JDialog();
                                tipsdialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                tipsdialog.setTitle("警告");
                                tipsdialog.setAlwaysOnTop(true);
                                Box boxlayout = Box.createVerticalBox();
                                int cost;
                                if (total < day) {
                                    cost = price * total + (2*price * (day - total));
                                    boxlayout.add(new JTextArea("您租车时间已超出预定时间,超出的天数将按原租金的2倍收取,总共需收取金额" + cost + "元"));
                                } else {
                                    cost = price * day;
                                    boxlayout.add(new JTextArea("您已租车" + day + "天,总共花费" + cost + "元"));
                                }
                                JButton positive = new JButton("确定");
                                positive.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        tipsdialog.setVisible(false);
                                        JDialog dialog = new JDialog();
                                        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                        if ((cash - cost) < 0) {
                                            dialog.setTitle("错误");
                                            dialog.add(new JLabel("余额不足,请充值后在进行此操作!"));
                                        } else {
                                            String info = Server.getString(Server.HOST + "/php/?deal=2&account=" + user.account + "&number=" + number);
                                            String result = Server.getString(Server.HOST + "/php/?update=2&field=Cash&n_account=" + user.account + "&newdata=" + (cash - cost));
                                            if (info.equals("successful") && result.equals("successful")) {
                                                dialog.setTitle("成功");
                                                dialog.add(new JLabel("已成功还车并退还保证金!请关闭对话框"));

                                                refresh();

                                            } else {
                                                dialog.setTitle("失败");
                                                dialog.add(new JLabel("请重新运行该系统再次尝试或联系工作人员!"));
                                            }
                                        }
                                        dialog.setSize(200, 180);
                                        dialog.setAlwaysOnTop(true);
                                        dialog.setLocationRelativeTo(null);
                                        dialog.setVisible(true);
                                    }
                                });
                                tipsdialog.setSize(200, 180);
                                boxlayout.add(positive);
                                tipsdialog.add(boxlayout);
                                tipsdialog.setLocationRelativeTo(null);
                                tipsdialog.setVisible(true);
                                b.setVisible(false);
                            }
                            catch (Exception exp) {
                                exp.printStackTrace();
                            }
                        }
                    }
                });
                buttonGroup.add(b);
                selectionDialog.add(layout);
                selectionDialog.setSize(200, 200);
                selectionDialog.setLocationRelativeTo(null);
                selectionDialog.setVisible(true);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        if (select == 2) {//修改按钮
            RegisterUser update = new RegisterUser(null, 2);
            update.account_editor.setText(user.account);
            update.name_editor.setText(user.name);
            update.password_editor.setText(user.password);
        }
        if (select == 3) {//充值按钮
            JDialog cashDialog = new JDialog();
            cashDialog.setTitle("充值");
            cashDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            JTextField cashEditor = new JTextField();
            Box layout = Box.createVerticalBox();
            JButton positiveButton = new JButton("确定");
            layout.add(new JLabel("请输入充值金额"));
            layout.add(cashEditor);
            Box buttonBox = Box.createHorizontalBox();
            buttonBox.add(Box.createHorizontalStrut(50));
            buttonBox.add(positiveButton);
            layout.add(buttonBox);
            cashDialog.add(layout);
            positiveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JDialog d = new JDialog();
                    d.setTitle("提示");
                    d.setSize(200, 100);
                    d.setLocationRelativeTo(null);
                    try{
                        int sumcash = Integer.parseInt(user.cash)+Integer.parseInt(cashEditor.getText());
                        String query = Server.HOST+"/php/?update=2&field=Cash&n_account="+user.account+"&newdata="+sumcash;
                        String info = Server.getString(query);
                        if (info.equals("successful")) {
                            mycash.setText("余额/元："+sumcash);
//                            cashEditor.setText("已经充值成功!");
                            d.add(new JLabel("已经充值成功!"));
                        }
                        else {
                            d.add(new JLabel("充值失败,请重试!"));
//                            cashEditor.setText("充值失败,请重试!");
                        }
                    } catch (Exception exp){
                        d.add(new JLabel("充值失败,请重试!"));
//                        cashEditor.setText("充值失败,请重试!");
                    } finally {
                        d.setLocationRelativeTo(null);
                        d.setVisible(true);
                        cashDialog.dispose();
                    }
                }
            });
            cashDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            cashDialog.setSize(200,100);
            cashDialog.setLocationRelativeTo(null);
            cashDialog.setVisible(true);
        }
        if (select == 4) {
            refresh();
        }
    }

    void refresh() {
        //重新启动新的窗口（代替刷新）
        String json = Server.getString(Server.HOST + "/php/?getinfomode=3&account=" + user.account);
        try {
            JSONObject all = new JSONObject(json);
            JSONObject data = all.getJSONObject("data");
            JSONArray users = data.getJSONArray("userinfo");
            JSONObject user = users.getJSONObject(0);
            father.setVisible(false);
            UserWindow userWindow = new UserWindow(((UserWindow)father).father, user);
            userWindow.setAlwaysOnTop(false);
//            System.out.println("普通用户登录");
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}

class MoreInfo extends MouseAdapter {//可租车辆列表鼠标监听器，即双击显示车辆详细信息
    CarsInfo[] carsInfos;
    MoreInfo(CarsInfo[] carsInfos) {
        this.carsInfos = carsInfos;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        JList list = (JList)e.getSource();
        if (e.getClickCount() == 2 && list.getSelectedIndex() >= 0) {
            int index = list.getSelectedIndex();
            RegisterCar showCar = new RegisterCar(null, 2);//传入参数2即选择详细信息模式
            showCar.number_editor.setText(carsInfos[index].number);
            showCar.style_editor.setText(carsInfos[index].style);
            showCar.able_editor.setText(carsInfos[index].able);
            showCar.brand_editor.setText(carsInfos[index].brand);
            showCar.name_editor.setText(carsInfos[index].name);
            showCar.cargo_editor.setText(carsInfos[index].cargo);
            showCar.people_editor.setText(carsInfos[index].people);
            showCar.price_editor.setText(carsInfos[index].price);
            showCar.deposit_editor.setText(carsInfos[index].deposit);
            showCar.distance_editor.setText(carsInfos[index].distance);
            if (!carsInfos[index].picture.equals("") && !(carsInfos[index].picture==null)) {
                String webpath = Server.HOST+"/php/cars/"+carsInfos[index].picture;
                showCar.image_label.file = new File(webpath);
                showCar.image_label.setImageIcon(Server.loadURLImage(webpath));
                showCar.image_label.isSetListene = false;
            }
            showCar.note_editor.setText(carsInfos[index].note);
        }
    }
}
