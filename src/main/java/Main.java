import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.crazzyghost.alphavantage.AlphaVantage;
import com.crazzyghost.alphavantage.Config;
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse;

public class Main {
    static String API_KEY = "1JODJUV5UQ859LYX";
    private String symbol = "NVDA";
    private TimeSeries series;
    private JFreeChart chart;

    public static void main(String[] args) {
        Main holder = new Main();
        Config cfg = Config.builder().key(API_KEY).timeOut(10).build();
        AlphaVantage.api().init(cfg);

        holder.series = new TimeSeries("Stock Series");
        TimeSeriesCollection dataset = new TimeSeriesCollection(holder.series);
        holder.chart = ChartFactory.createTimeSeriesChart(
            holder.symbol + " Stock", "Date", "Price", dataset, true, true, false
        );

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Stock Chart");
            JTextField stockSymbolInput = new JTextField(10);
            JButton fetchButton = new JButton("Go");
            
            JToolBar toolbar = new JToolBar();
            toolbar.add(new JLabel("Stock Symbol: "));
            toolbar.add(stockSymbolInput);
            toolbar.add(fetchButton);

            fetchButton.addActionListener(e -> {
                holder.symbol = stockSymbolInput.getText().toUpperCase();
                holder.updateChartData();
            });

            frame.add(toolbar, BorderLayout.NORTH);
            frame.add(new ChartPanel(holder.chart), BorderLayout.CENTER);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            holder.updateChartData();
        });
    }

    public void updateChartData() {
        AlphaVantage.api()
            .timeSeries()
            .weekly()
            .forSymbol(symbol)
            .onSuccess(e -> {
                TimeSeriesResponse response = (TimeSeriesResponse) e;
                SwingUtilities.invokeLater(() -> {
                    series.setNotify(false);
                    series.clear();
                    response.getStockUnits().forEach(unit -> {
                        series.addOrUpdate(new Day(java.sql.Date.valueOf(unit.getDate())), unit.getClose());
                    });
                    series.setNotify(true);
                    chart.setTitle(symbol + " Stock");
                });
            })
            .fetch();
    }
}
