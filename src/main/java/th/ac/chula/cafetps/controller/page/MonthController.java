package th.ac.chula.cafetps.controller.page;

import com.sun.javafx.charts.Legend;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import th.ac.chula.cafetps.constants.ItemCategory;
import th.ac.chula.cafetps.helper.SummaryHelper;

import java.time.YearMonth;
import java.util.*;

import static th.ac.chula.cafetps.Utility.formatLabelText;

public class MonthController extends SwitchController {

    @FXML
    private LineChart<Integer, Integer> chart;

    @FXML
    private Label monthNameLabel;

    @FXML
    private Label yearLabel;

    @FXML
    private Label totalUnitLabel;

    @FXML
    private Label nonUnitLabel;

    @FXML
    private Label coffeeUnitLabel;

    @FXML
    private Label bakeryUnitLabel;

    @FXML
    private Label saleIncomeLabel;

    @FXML
    private Label saleCostLabel;

    @FXML
    private Label rentalCostLabel;

    @FXML
    private Label salaryLabel;

    @FXML
    private Label netTotalamount;

    @FXML
    private Label netTotalLabel;

    @FXML
    private ComboBox<String> selectorBox;

    @FXML
    private NumberAxis yaxis;

    @FXML
    private NumberAxis xaxis;

    @FXML
    private Label yearLabel2;

    @FXML
    private Label monthNameLabel2;

    @FXML
    private Label newMemberInThisMonth;

    @FXML
    private Label newMemberEngagement;

    @FXML
    private Label profitFromNewMember;

    @FXML
    private Label profitFromMember;

    @FXML
    private Label profitFromGuest;

    @FXML
    private Label bestSellerCoffee;

    @FXML
    private Label bestSellerNonCoffee;

    @FXML
    private Label bestSellerBakery;


    public static final String[] monthOf = new String[]{"มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน", "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"};

    public void init() {
        initMonthSelection();
        updateGraph();
    }

    public void initLabels(){
        updateSelectedMonth();
        updateSellUnit();
        updateIncomeCostProfit();
        updateBestSeller();
        updateMemberAnalysis();
    }

    public void updateGraph(){
        chart.getData().clear();
        initLabels();
        refreshChart();
    }

    public void refreshChart(){
        HashMap<String, ArrayList<XYChart.Data<Integer,Integer>>> values = SummaryHelper.getChartData(selectorBox.getValue());

        chart.getData().add(getMockLine(YearMonth.parse(selectorBox.getValue())));

        chart.getData().add(getDataLine(values.get("bakery"),"Bakery"));
        chart.getData().add(getDataLine(values.get("coffee"),"Coffee"));
        chart.getData().add(getDataLine(values.get("noncoffee"),"Non-Coffee"));

        int maxY[] = new int[3];

        maxY[0] = chart.getData().stream().flatMap(s -> s.getData().stream())
                .max(Comparator.comparing(XYChart.Data::getYValue))
                .map(XYChart.Data::getYValue).orElse(Integer.MIN_VALUE);

        yaxis.setAutoRanging(false);
        yaxis.setUpperBound((maxY[0]/5+1)*5);
        xaxis.setAutoRanging(false);
        xaxis.setUpperBound((YearMonth.parse(selectorBox.getValue()).lengthOfMonth()+1));
        xaxis.setTickUnit(1);
        chart.getData().remove(0);
        chart.setAnimated(false);

        chart.getChildrenUnmodifiable().stream()
                .filter(Legend.class::isInstance)
                .map(Legend.class::cast)
                .flatMap(l -> l.getItems().stream())
                .map(li -> chart.getData().stream()
                        .filter(s -> s.getName().equals(li.getText())).findFirst()
                        .map(s -> new Pair<>(li, s)))
                .filter(Optional::isPresent).map(Optional::get)
                .forEach(p -> prepareLegendToggle(p.getKey(), p.getValue()));
    }

    private void updateMemberAnalysis(){
        String yearMonth = selectorBox.getValue();
        formatLabelText(newMemberInThisMonth,SummaryHelper.getNewMemberThisMonth(yearMonth));
        formatLabelText(newMemberEngagement,SummaryHelper.getNewMemberEngagementThisMonth(yearMonth));
        formatLabelText(profitFromMember,SummaryHelper.profitFromMember(yearMonth));
        formatLabelText(profitFromNewMember,SummaryHelper.profitFromNewMember(yearMonth));
        formatLabelText(profitFromGuest,SummaryHelper.profitFromGuest(yearMonth));
    }
    private void updateBestSeller(){
        String yearMonth = selectorBox.getValue();
        formatLabelText(bestSellerBakery,SummaryHelper.getBestSellerInMonth(ItemCategory.BAKERY,yearMonth)[0]);
        formatLabelText(bestSellerCoffee,SummaryHelper.getBestSellerInMonth(ItemCategory.COFFEE,yearMonth)[0]);
        formatLabelText(bestSellerNonCoffee,SummaryHelper.getBestSellerInMonth(ItemCategory.NONCOFFEE,yearMonth)[0]);
    }

    private void initMonthSelection(){
        ArrayList<String> values = SummaryHelper.getDistinctYearMonth();
        selectorBox.setItems(FXCollections.observableArrayList(values));
        selectorBox.setValue(values.get(values.size()-1));
    }

    private void updateSelectedMonth(){
        YearMonth ym = YearMonth.parse(selectorBox.getValue());

        monthNameLabel.setText(monthOf[ym.getMonthValue()-1]);
        monthNameLabel2.setText(monthOf[ym.getMonthValue()-1]);
        yearLabel.setText(String.valueOf(ym.getYear()));
        yearLabel2.setText(String.valueOf(ym.getYear()));
    }

    private void updateSellUnit(){
        HashMap<String,Integer> values = SummaryHelper.getSellUnit(selectorBox.getValue());
        int bakeryAMT = values.get("bakery")== null ? 0:values.get("bakery");
        int coffeeAMT = values.get("coffee")== null ? 0:values.get("coffee");
        int nonCoffeeAMT = values.get("noncoffee")== null ? 0:values.get("noncoffee");
        int total = bakeryAMT + coffeeAMT + nonCoffeeAMT;
        formatLabelText(bakeryUnitLabel,bakeryAMT);
        formatLabelText(coffeeUnitLabel,coffeeAMT);
        formatLabelText(nonUnitLabel,nonCoffeeAMT);
        formatLabelText(totalUnitLabel,total);
    }

    private void updateIncomeCostProfit(){
        ArrayList<Integer> ic = SummaryHelper.getIncomeAndCost(selectorBox.getValue());

        int income = ic.get(0);
        int sale_cost = ic.get(1);
        formatLabelText(saleIncomeLabel,income);
        formatLabelText(saleCostLabel,sale_cost);

        int rental = SummaryHelper.getRentalCost();
        formatLabelText(rentalCostLabel,rental);

        int salary = SummaryHelper.getEmployeeTotalSalary();
        formatLabelText(salaryLabel,salary);

        int netTotal = income - (sale_cost + salary + rental);
        if(netTotal < 0){
            netTotalLabel.setText("ขาดทุนสุทธิ");
            netTotalamount.setTextFill(Color.RED);
        } else {
            netTotalamount.setTextFill(Color.GREEN);
        }
        formatLabelText(netTotalamount,netTotal);
    }

    private static XYChart.Series<Integer,Integer> getMockLine(YearMonth yearMonth){
        XYChart.Series<Integer,Integer> temp = new XYChart.Series<>();
        for(int i = 1;i<yearMonth.lengthOfMonth()+1;i++){
            temp.getData().add(new XYChart.Data<>(i,0));
        }

        return  temp;
    }

    private static XYChart.Series<Integer,Integer> getDataLine(ArrayList<XYChart.Data<Integer,Integer>> list, String displayName){
        XYChart.Series<Integer,Integer> line = new XYChart.Series<>(FXCollections.observableList(list));
        line.setName(displayName);
        return line;
    }

    private static void prepareLegendToggle(Legend.LegendItem li, XYChart.Series<Integer,Integer> s){
        li.getSymbol().setCursor(Cursor.HAND); // Hint user that legend symbol is clickable
        li.getSymbol().setOnMouseClicked(me -> {
            if (me.getButton().equals(MouseButton.PRIMARY)) {
                s.getNode().setVisible(!s.getNode().isVisible()); // Toggle visibility of line
                s.getData().stream().map(XYChart.Data::getNode).filter(Objects::nonNull)
                        .forEach(n -> n.setVisible(s.getNode().isVisible())); // Toggle visibility of every node in the series
            }
        });
    }


}
