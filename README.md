README CREATED WITH AI SINCE I'M A **L**a**Z**y **B**u**M**™
PROJECT WRITTEN BY MYSELF THOUGH, NO AI IN ANYTHING BUT THE README

# Stock Trading Simulator

A comprehensive paper trading website that simulates real stock market trading using live data from the Alpha Vantage API. Built with a Java Spring Boot backend and a modern HTML/CSS/JavaScript frontend, featuring user authentication, real-time charts, and advanced trading tools.

## Features

- **User Authentication**: Sign in with Google or GitHub accounts
- **Real-Time Stock Data**: Live stock prices, charts, and fundamentals from Alpha Vantage API
- **Paper Trading**: Buy and sell stocks with virtual cash without real financial risk
- **Limit Orders**: Place buy/sell orders that execute automatically when price targets are reached
- **Portfolio Management**: Track holdings, cash balance, and total portfolio value
- **Interactive Charts**: Candlestick and line charts with zoom and pan using Chart.js
- **Leaderboard**: Compete with other users based on percentage gains
- **Session Management**: Start new trading sessions with custom usernames and starting cash
- **Dark/Light Mode**: Toggle between themes for better user experience

### Prerequisites
- a compooper, a browser, and the ability to move your hands

## Usage
**LINK** located at stock-sim-production.up.railway.app (its not a free hosting though, so I will either switch to github/some other provider or the site will just be gone in a few days to weeks)
1. **Sign In**: Use Google or GitHub authentication
2. **Start Trading**: Enter a username and starting cash amount
3. **Load Stocks**: Enter a stock symbol (e.g., AAPL) and press Enter
4. **Trade**: Use the buy/sell buttons or place limit orders
5. **Monitor Portfolio**: View transactions and total value
6. **Compete**: Check the leaderboard for top performers

### Limit Orders
- Click "Show Limit Orders" to reveal the form
- Select BUY or SELL, enter limit price and quantity
- Orders execute automatically when market conditions are met
- View and cancel pending orders
- Orders expire after 24 hours if not filled

## Technologies

- **Backend**: Spring Boot, Java 21
- **Frontend**: HTML5, CSS3, JavaScript (ES6+)
- **Database**: Google Firestore
- **Charts**: Chart.js with financial chart support
- **Authentication**: Firebase Auth
- **API**: Alpha Vantage for stock data
- **Build Tool**: Gradle

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Create a Pull Request

## Disclaimer

This is a simulation for educational purposes only. It does not constitute financial advice, and all trading is virtual with no real monetary value.