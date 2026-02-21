import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.*;
import org.jfree.chart.*;
import org.jfree.ui.ApplicationFrame;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
public class Main {
    static String API_KEY = "1JODJUV5UQ859LYX";
    public static void main(String[] args) throws MalformedURLException {
        String symbol = "AAPL";
        String timeframe = "TIME_SERIES_DAILY";
        String urlStr = 
            "https://www.alphavantage.co/query?function=" + 
            timeframe + 
            "&symbol=" 
            + symbol + 
            "&apikey=" 
            + API_KEY;
        JFrame frame = new JFrame("Stock Simulator");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setVisible(true);
    }
}