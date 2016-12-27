import java.io.*;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.sql.*;
import yahoofinance.*;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class test {
	public static void main(String[] args) throws IOException {
		Connection conn = connectDB();
		Statement stmt;

		// Statement�� �����´�.
		try {

			stmt = conn.createStatement();
			BufferedReader br = new BufferedReader(
					new FileReader("C:/Users/ALIENWARE/Desktop/workspace/realsymbol.txt"));

			while (true) {
				String line = br.readLine();
				if (line == null)
					break;

				Stock stock;
				try {

					stock = YahooFinance.get(line);
					String query;
					List<HistoricalQuote> queue = stock.getHistory(Interval.DAILY);
					for (int i = 0; i < queue.size(); i++) {
						HistoricalQuote temp = queue.get(i);
						query = "insert into realstock values ('" + line + "','" + convertDate(temp.getDate()) + "',"
								+ temp.getOpen().toString() + "," + temp.getHigh().toString() + ","
								+ temp.getLow().toString() + "," + temp.getClose().toString() + ")";
						System.out.println(query);

						stmt.executeQuery(query);

					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			br.close();

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public static String convertDate(Calendar calen) {
		String ret = String.valueOf(calen.get(Calendar.YEAR));
		if (calen.get(Calendar.MONTH) < 9) {
			ret = ret.concat("0");
			ret = ret.concat(String.valueOf(calen.get(Calendar.MONTH) + 1));

		} else {
			ret = ret.concat(String.valueOf(calen.get(Calendar.MONTH) + 1));
		}
		if (calen.get(Calendar.DATE) < 10) {
			ret = ret.concat("0");
			ret = ret.concat(String.valueOf(calen.get(Calendar.DATE)));

		} else {
			ret = ret.concat(String.valueOf(calen.get(Calendar.DATE)));
		}
		return ret;
	}

	public static Connection connectDB() {
		String DB_URL = "jdbc:oracle:thin:@127.0.0.1:1521:jk";
		String DB_USER = "jk";
		String DB_PASSWORD = "powerkk";

		try {
			// ����̹��� �ε��Ѵ�.
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			// �����ͺ��̽��� ������ �����Ѵ�.
			return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}