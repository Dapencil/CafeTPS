package th.ac.chula.cafetps.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import th.ac.chula.cafetps.*;
import th.ac.chula.cafetps.MenuItem;
import java.io.IOException;
import java.util.ArrayList;

public class HomeOrderController extends SwitchController{


    @FXML
    private Label memberName;

    @FXML
    private GridPane recentGrid;

    @FXML
    private GridPane coffeeGrid;

    @FXML
    private GridPane noncoffeeGrid;

    @FXML
    private GridPane bakeryGrid;

    @FXML
    private TableView<Item> receiptTable;

    @FXML
    private TableColumn<Item,String> nameItemCol;

    @FXML
    private TableColumn<Item,String> sweetnessCol;

    @FXML
    private TableColumn<Item,Integer> quantityCol;

    @FXML
    private TableColumn<Item,Integer> priceCol;

    @FXML
    private TableColumn<Item,String> editCol;

    @FXML
    private Label totalPrice;

    @FXML
    private VBox recentBox;


    private Member member;


    private ArrayList<PickItem> data;

    private ObservableList<Item> receiptShow;

    private static final String deletePath = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm5 11H7v-2h10v2z";


    @FXML
    public void initialize(){
        receiptShow = FXCollections.observableArrayList();

        nameItemCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        sweetnessCol.setCellValueFactory(new PropertyValueFactory<>("sweetness"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        receiptTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //add cell of button edit
        Callback<TableColumn<Item, String>, TableCell<Item, String>> cellFactory = (TableColumn<Item, String> param) -> {
            // make cell containing buttons
            final TableCell<Item, String> cell = new TableCell<Item, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    //that cell created only on non-empty rows
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        SVGPath deleteIcon = new SVGPath();
                        deleteIcon.setContent(deletePath);
                        deleteIcon.setStyle(
                                " -fx-cursor: hand ;"
                                        + "-glyph-size:24px;"
                                        + "-fx-fill:#FF0000;"
                                        + "-fx-opacity: 54%;"
                        );

                        deleteIcon.setOnMouseClicked((MouseEvent event) -> {
                            Item selectedItem = receiptTable.getSelectionModel().getSelectedItem();
                            receiptTable.getItems().remove(selectedItem);
                            totalPrice.setText(getTotal()+"");
                        });

                        setGraphic(deleteIcon);
                        setText(null);

                    }
                }

            };

            return cell;
        };

        nameItemCol.setCellFactory(new Callback<TableColumn<Item, String>, TableCell<Item, String>>() {

            @Override
            public TableCell<Item, String> call(
                    TableColumn<Item, String> param) {
                TableCell<Item, String> cell = new TableCell<>();
                Text text = new Text();
                cell.setGraphic(text);
                cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                text.wrappingWidthProperty().bind(cell.widthProperty());
                text.textProperty().bind(cell.itemProperty());
                return cell ;
            }
        });

        sweetnessCol.setResizable(false);
        nameItemCol.setResizable(false);
        quantityCol.setResizable(false);
        priceCol.setResizable(false);
        editCol.setResizable(false);

        sweetnessCol.setStyle("-fx-font-size: 14px;");
        editCol.setCellFactory(cellFactory);
        receiptTable.setItems(receiptShow);
        receiptTable.widthProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth)
            {
                TableHeaderRow header = (TableHeaderRow) receiptTable.lookup("TableHeaderRow");
                header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        header.setReordering(false);
                    }
                });
            }
        });

        totalPrice.setText(getTotal()+"");
    }

    public void init(){
//        member = helper.memberCheck("0842053668");
        MenuItem temp = new MenuItem(helper,member);
        ArrayList<Item> recentOrder = temp.getRecentOrder();
        int column = 0;
        int row = 1;
        if(member.getMemberID().equals("0")){
            memberName.setText("ไม่ได้เป็นสมาชิก");
            recentBox.setVisible(false);
        }else{
            memberName.setText("ลูกค้า คุณ"+member.getMemberName());
            try {
                for(int i = 0;i< 4;i++){
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/th/ac/chula/cafetps/recent_card.fxml"));
                    AnchorPane pane = loader.load();
                    RecentCardController recentCardController = loader.getController();
                    if(i<recentOrder.size()){
                        recentCardController.setData(recentOrder.get(i));
                        recentGrid.add(pane, i, 1); //(child,column,row)
                        pane.setOnMouseClicked((MouseEvent event) -> {
                            Item item = recentCardController.item;
                            item.setPricePerUnit(helper.getPriceTable().getPrice(item.getOnlyName(),item.getProperty()));
                            addItem(recentCardController.item);
                        });
                    }
                }
                recentGrid.setHgap(16);
                recentGrid.setPadding(new Insets(0,36,0,40));
                recentGrid.setMaxWidth(160*recentGrid.getColumnCount());

                column = 0;
                row = 1;
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        data = temp.getCoffeeMenu();
        try {
            for (int i = 0; i < data.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/th/ac/chula/cafetps/card.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                CardController cardController = fxmlLoader.getController();
                cardController.setData(data.get(i));
                anchorPane.setOnMouseClicked((MouseEvent event) -> {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/th/ac/chula/cafetps/add_item.fxml"));
                    Stage addPopupStage = new Stage();
                    try {
                        Scene addScene = new Scene(loader.load(), 326, 499);
                        AddItemController controller = loader.getController();
                        addPopupStage.initModality(Modality.APPLICATION_MODAL);
                        addPopupStage.initStyle(StageStyle.UNDECORATED);
                        addPopupStage.setScene(addScene);
                        controller.setData(cardController.pickItem);
                        addPopupStage.show();
                        controller.addButton.setOnMouseClicked((MouseEvent e) ->{
                            if(controller.propertyBox.getValue()==null || controller.sweetnessBox.getValue()==null){
                                controller.alertmsg.setText("กรุณาเลือกข้อมูลให้ครบถ้วน");
                            }else{
                                Item item = new Item(cardController.pickItem.getName(),controller.getPropertyFromBox(),controller.getQuantity(),controller.getSweetness());
                                item.setPricePerUnit(helper.getPriceTable().getPrice(item.getOnlyName(),item.getProperty()));
                                addItem(item);
                                addPopupStage.close();
                            }
                        });
                    } catch (IOException e){
                        e.printStackTrace();
                    }

                });
                if (column == 6) {
                    column = 0;
                    row++;
                }
                coffeeGrid.add(anchorPane, column++, row); //(child,column,row)
                coffeeGrid.setHgap(8);
                coffeeGrid.setVgap(16);
                coffeeGrid.setPadding(new Insets(0,36,0,40));
            }
            data = temp.getNonCoffeeMenu();
            column = 0;
            row = 1;
            for (int i = 0; i < data.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/th/ac/chula/cafetps/card.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                CardController cardController = fxmlLoader.getController();
                cardController.setData(data.get(i));
                anchorPane.setOnMouseClicked((MouseEvent event) -> {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/th/ac/chula/cafetps/add_item.fxml"));
                    Stage addPopupStage = new Stage();
                    try {
                        Scene addScene = new Scene(loader.load(), 326, 499);
                        AddItemController controller = loader.getController();
                        addPopupStage.initModality(Modality.APPLICATION_MODAL);
                        addPopupStage.initStyle(StageStyle.UNDECORATED);
                        addPopupStage.setScene(addScene);
                        controller.setData(cardController.pickItem);
                        addPopupStage.show();
                        controller.addButton.setOnMouseClicked((MouseEvent e) ->{
                            if(controller.propertyBox.getValue()==null || controller.sweetnessBox.getValue()==null){
                                controller.alertmsg.setText("กรุณาเลือกข้อมูลให้ครบถ้วน");
                            }else {
                                Item item = new Item(cardController.pickItem.getName(), controller.getPropertyFromBox(), controller.getQuantity(), controller.getSweetness());
                                item.setPricePerUnit(helper.getPriceTable().getPrice(item.getOnlyName(), item.getProperty()));
                                addItem(item);
                                addPopupStage.close();
                            }
                        });
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                });
                if (column == 6) {
                    column = 0;
                    row++;
                }
                noncoffeeGrid.add(anchorPane, column++, row); //(child,column,row)
            }
            noncoffeeGrid.setHgap(8);
            noncoffeeGrid.setVgap(16);
            noncoffeeGrid.setPadding(new Insets(0,36,0,40));
            data = temp.getBakeryMenu();
            column = 0;
            row = 1;
            for (int i = 0; i < data.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/th/ac/chula/cafetps/card.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                CardController cardController = fxmlLoader.getController();
                cardController.setData(data.get(i));
                anchorPane.setOnMouseClicked((MouseEvent event) -> {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/th/ac/chula/cafetps/add_item.fxml"));
                    Stage addPopupStage = new Stage();
                    try {
                        Scene addScene = new Scene(loader.load(), 326, 499);
                        AddItemController controller = loader.getController();
                        addPopupStage.initModality(Modality.APPLICATION_MODAL);
                        addPopupStage.initStyle(StageStyle.UNDECORATED);
                        addPopupStage.setScene(addScene);
                        controller.setData(cardController.pickItem);
                        addPopupStage.show();
                        controller.addButton.setOnMouseClicked((MouseEvent e) ->{
                            Item item = new Item(cardController.pickItem.getName(),itemProperty.NONE,controller.getQuantity(),"");
                            item.setPricePerUnit(helper.getPriceTable().getPrice(item.getOnlyName(),item.getProperty()));
                            addItem(item);
                            addPopupStage.close();
                        });
                    } catch (IOException e){
                        e.printStackTrace();
                    }

                });
                if (column == 6) {
                    column = 0;
                    row++;
                }
                bakeryGrid.add(anchorPane, column++, row); //(child,column,row)
                bakeryGrid.setHgap(8);
                bakeryGrid.setVgap(16);
                bakeryGrid.setPadding(new Insets(0,36,0,40));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getTotal(){
        int total = 0;
        for(Item item : receiptShow){
            total += item.getPrice();
        }
        return total;
    }

    public void submitOrder() throws InterruptedException{
        if(receiptShow.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/th/ac/chula/cafetps/stylesheet.css").toExternalForm());
            alert.setContentText("ไม่มีสินค้า");
            alert.initStyle(StageStyle.UNDECORATED);
            alert.show();
        }else {
            int total = getTotal();
            FXMLLoader firstSceneLoader = new FXMLLoader(CafeTPSApplication.class.getResource("/th/ac/chula/cafetps/pay_first.fxml"));
            FXMLLoader secondSceneLoader = new FXMLLoader(CafeTPSApplication.class.getResource("/th/ac/chula/cafetps/pay_second.fxml"));
            try {
                Scene pay1Scene = new Scene(firstSceneLoader.load(), 326, 448);
                Scene pay2Scene = new Scene(secondSceneLoader.load(), 326, 448);
                PayFirstController payFirstController = firstSceneLoader.getController();
                PaySecondController paySecondController = secondSceneLoader.getController();
                payFirstController.setAmount(total);
                Stage payPopup = new Stage();
                payPopup.initModality(Modality.APPLICATION_MODAL);
                payPopup.initStyle(StageStyle.UNDECORATED);
                payPopup.setScene(pay1Scene);
                payFirstController.next.setOnMouseClicked((MouseEvent event) -> {
                    String temp = payFirstController.getMoneyField.getText();
                    if(temp.equals("")||!helper.isNumeric(temp)){
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.getDialogPane().getStylesheets().add(getClass().getResource("/th/ac/chula/cafetps/stylesheet.css").toExternalForm());
                        alert.setContentText("กรุณากรอกจำนวนเงิน");
                        alert.initStyle(StageStyle.UNDECORATED);
                        alert.show();
                    }else{
                        int gottenMoney = Integer.parseInt(temp);
                        if(gottenMoney < total) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.getDialogPane().getStylesheets().add(getClass().getResource("/th/ac/chula/cafetps/stylesheet.css").toExternalForm());
                            alert.setContentText("กรุณารับเงินให้ถูกต้อง");
                            alert.initStyle(StageStyle.UNDECORATED);
                            alert.show();
                        }else{
                            paySecondController.init(gottenMoney, total);
                            payPopup.setScene(pay2Scene);
                        }
                    }
                });
                paySecondController.submit.setOnMouseClicked((MouseEvent event) -> {
                    helper.insertReceipt(employee,member,getTotal());
                    helper.insertReceiptDetail(receiptShow);
                    member.setPoint(member.getPoint()+paySecondController.getPoint());
                    helper.updatePoint(member);
                    payPopup.close();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/th/ac/chula/cafetps/home.fxml"));
                    try{
                        root = loader.load();
                        HomeController homeController = loader.getController();
                        homeController.setHelper(helper);
                        homeController.setEmployee(employee);
                        scene = new Scene(root);
                        stage.setScene(scene);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                });
                payPopup.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void addItem(Item item){
        receiptShow.add(item);
        totalPrice.setText(getTotal()+"");
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }


}