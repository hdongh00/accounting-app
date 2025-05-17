import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SpinnerDateModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class App {
	private final String ID = "Danny";
	private final String PASS = "abc";
	private JFrame frame;
	private JTextField idField;
	private JPasswordField passField;
	private JPanel currPanel;
	private JTextField nameInput;
	private JTextField amountInput;
	private JTextField searchInput;
	private JTable table;
	private TableData td;
	private JComboBox typeInput;
	private List<String> categoryList = new ArrayList<>(); // 카테고리 목록
	private int loggedInUserId; // 로그인한 사용자의 ID

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App window = new App();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public App() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		td = new TableData(loggedInUserId);
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ImagePanel loginPanel = new ImagePanel(
				new ImageIcon("C:/Users/hdong/eclipse-workspace/AccountingApp/Image Sources/theme.jpg").getImage());

		currPanel = loginPanel;
		ImagePanel tranPanel = new ImagePanel(
				new ImageIcon("C:/Users/hdong/eclipse-workspace/AccountingApp/Image Sources/Activation.jpg")
						.getImage());

		frame.setSize(loginPanel.getDim());
		frame.setPreferredSize(loginPanel.getDim());
		ImagePanel sumPanel = new ImagePanel(
				new ImageIcon("C:/Users/hdong/eclipse-workspace/AccountingApp/Image Sources/Activation.jpg")
						.getImage());
		frame.getContentPane().add(sumPanel);

		sumPanel.setVisible(false);

		// Summary

		JButton tranBtn = new JButton("");
		tranBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currPanel.setVisible(false);
				tranPanel.setVisible(true);
				currPanel = tranPanel;
			}
		});
		tranBtn.setIcon(new ImageIcon("C:/Users/hdong/eclipse-workspace/AccountingApp/Image Sources/Transaction.jpg"));
		tranBtn.setBounds(27, 191, 246, 40);
		tranBtn.setBorder(null);
		sumPanel.add(tranBtn);

		JButton chartBtn = new JButton("Show Chart");
		chartBtn.setBounds(29, 252, 228, 40);
		sumPanel.add(chartBtn);

		chartBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showPieAndBarChart();
			}
		});
		// 월별 비교 그래프 버튼
		JButton monthlyExpenseBtn = new JButton("Show MonthChart");
		monthlyExpenseBtn.setBounds(29, 300, 228, 40);
		sumPanel.add(monthlyExpenseBtn);

		monthlyExpenseBtn.addActionListener(e -> showMonthlyExpenseChart());

		/*
		 * JButton barBtn = new JButton("Show BarChart"); barBtn.setBounds(29, 290, 259,
		 * 40); sumPanel.add(barBtn);
		 * 
		 * barBtn.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent e) { showBarChart(); } });
		 */

		JLabel lblSearch = new JLabel("Search :");
		lblSearch.setFont(new Font("Tahoma", Font.PLAIN, 22));
		lblSearch.setBounds(317, 75, 83, 40);
		sumPanel.add(lblSearch);

		searchInput = new JTextField();
		searchInput.setFont(new Font("Malgun Gothic", Font.PLAIN, 22));
		searchInput.setBounds(413, 76, 979, 40);
		sumPanel.add(searchInput);
		searchInput.setColumns(10);

		// 순수익 계산
		JLabel summaryLabel = new JLabel("이번 달 순수익: ");
		summaryLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 22));
		summaryLabel.setBounds(337, 620, 600, 30);
		sumPanel.add(summaryLabel);

		table = new JTable(td);
		table.setBounds(337, 140, 1155, 445);
		table.setRowHeight(30);
		table.setFont(new Font("Sansserif", Font.BOLD, 15));
		table.setPreferredScrollableViewportSize(new Dimension(1155, 430));
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(302, 138, 1090, 432);
		sumPanel.add(scrollPane);

		JPanel tp = new JPanel();
		tp.setBounds(293, 138, 10, 430);
		sumPanel.add(tp);
		tp.setOpaque(false);

		JTableHeader header = table.getTableHeader();
		header.setBackground(new Color(92, 179, 255));
		header.setForeground(new Color(255, 255, 255));
		header.setFont(new Font("Sansserif", Font.BOLD, 15));

		searchInput.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				String search = searchInput.getText();
				TableRowSorter<AbstractTableModel> trs = new TableRowSorter<>(td);
				table.setRowSorter(trs);
				trs.setRowFilter(RowFilter.regexFilter(search));
			}
		});

		frame.getContentPane().add(loginPanel);

		tranPanel.setVisible(false);
		frame.getContentPane().add(tranPanel);

		idField = new JTextField();
		idField.setFont(new Font("Tahoma", Font.PLAIN, 26));
		idField.setBounds(1118, 325, 264, 43);
		loginPanel.add(idField);
		idField.setColumns(10);
		idField.setBorder(null);

		passField = new JPasswordField();
		passField.setFont(new Font("Tahoma", Font.PLAIN, 26));
		passField.setBounds(1118, 408, 264, 43);
		passField.setBorder(null);
		loginPanel.add(passField);

		JCheckBox chckbxNewCheckBox = new JCheckBox("");
		chckbxNewCheckBox.setBounds(1081, 461, 25, 25);
		loginPanel.add(chckbxNewCheckBox);

		JButton logInBtn = new JButton("");
		logInBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String inputId = idField.getText();
				String inputPass = new String(passField.getPassword());
				String hashedPass = hashPassword(inputPass);

				try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root",
						"1234");
						PreparedStatement ps = conn
								.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
					ps.setString(1, inputId);
					ps.setString(2, inputPass);

					ResultSet rs = ps.executeQuery();
					if (rs.next()) {
						loggedInUserId = rs.getInt("id"); // userId 저장
						td = new TableData(loggedInUserId);
						table.setModel(td);
						td.updateList();
						currPanel.setVisible(false);
						sumPanel.setVisible(true);
						currPanel = sumPanel;
						updateNetIncome(summaryLabel);
					} else {
						JOptionPane.showMessageDialog(null, "로그인 실패: 아이디 또는 비밀번호가 틀렸습니다.");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "DB 오류: " + ex.getMessage());
				}
			}
		});

		logInBtn.setBorder(null);
		logInBtn.setBounds(1081, 488, 301, 38);
		loginPanel.add(logInBtn);
		logInBtn.setIcon(new ImageIcon("C:/Users/hdong/eclipse-workspace/AccountingApp/Image Sources/button.jpg"));
		logInBtn.setPressedIcon(
				new ImageIcon("C:/Users/hdong/eclipse-workspace/AccountingApp/Image Sources/btnClicked.jpg"));

		JButton signupBtn = new JButton("회원가입");
		signupBtn.setBounds(1221, 536, 161, 38); // 위치 조정
		loginPanel.add(signupBtn);
		signupBtn.addActionListener(e -> showSignupPanel());

		// Transaction

		JButton sumBtn = new JButton("");
		sumBtn.setIcon(new ImageIcon("C:/Users/hdong/eclipse-workspace/AccountingApp/Image Sources/Summary.jpg"));
		sumBtn.setBorder(null);
		sumBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currPanel.setVisible(false);
				sumPanel.setVisible(true);
				currPanel = sumPanel;
			}
		});
		sumBtn.setBounds(20, 130, 247, 40);
		tranPanel.add(sumBtn);

		JLabel lblName = new JLabel("Name");
		lblName.setFont(new Font("Tahoma", Font.PLAIN, 33));
		lblName.setBounds(320, 125, 139, 49);
		tranPanel.add(lblName);

		JLabel lblType = new JLabel("Type");
		lblType.setFont(new Font("Tahoma", Font.PLAIN, 33));
		lblType.setBounds(320, 204, 139, 49);
		tranPanel.add(lblType);

		JLabel lblAmount = new JLabel("Amount");
		lblAmount.setFont(new Font("Tahoma", Font.PLAIN, 33));
		lblAmount.setBounds(320, 284, 139, 49);
		tranPanel.add(lblAmount);

		JLabel lblNote = new JLabel("Note");
		lblNote.setFont(new Font("Tahoma", Font.PLAIN, 33));
		lblNote.setBounds(320, 370, 139, 49);
		tranPanel.add(lblNote);

		nameInput = new JTextField();
		nameInput.setFont(new Font("Malgun Gothic", Font.PLAIN, 33));
		// nameInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
		nameInput.setBounds(452, 130, 912, 49);
		tranPanel.add(nameInput);
		nameInput.setColumns(10);

		typeInput = new JComboBox();
		loadExpenseCategories(); // 카테고리 로딩
		typeInput.setFont(new Font("Malgun Gothic", Font.PLAIN, 33));
		// typeInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
		typeInput.setBounds(452, 203, 912, 49);
		tranPanel.add(typeInput);
		typeInput.setBackground(Color.WHITE);

		amountInput = new JTextField();
		amountInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
		amountInput.setColumns(10);
		amountInput.setBounds(452, 284, 912, 49);
		tranPanel.add(amountInput);

		JTextArea noteInput = new JTextArea();
		noteInput.setFont(new Font("Malgun Gothic", Font.PLAIN, 33));
		// noteInput.setFont(new Font("Courier New", Font.PLAIN, 33));
		noteInput.setBounds(452, 370, 912, 60);
		tranPanel.add(noteInput);
		noteInput.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		// 날짜 입력 라벨
		JLabel lblDate = new JLabel("Date");
		lblDate.setFont(new Font("Tahoma", Font.PLAIN, 33));
		lblDate.setBounds(320, 464, 139, 49);
		tranPanel.add(lblDate);

		// 날짜 선택 스피너
		SpinnerDateModel dateModel = new SpinnerDateModel();
		JSpinner dateInput = new JSpinner(dateModel);
		JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateInput, "yyyy-MM-dd");
		dateInput.setEditor(dateEditor);
		dateInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
		dateInput.setBounds(452, 464, 300, 49);
		tranPanel.add(dateInput);

		// 시간 입력 라벨
		JLabel lblTime = new JLabel("Time");
		lblTime.setFont(new Font("Tahoma", Font.PLAIN, 33));
		lblTime.setBounds(320, 523, 139, 49);
		tranPanel.add(lblTime);

		// 시간 선택 스피너
		SpinnerDateModel timeModel = new SpinnerDateModel();
		JSpinner timeInput = new JSpinner(timeModel);
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeInput, "HH:mm:ss");
		timeInput.setEditor(timeEditor);
		timeInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
		timeInput.setBounds(452, 523, 300, 49);
		tranPanel.add(timeInput);

		JButton btnNewButton = new JButton("SUBMIT");

		// 거래 추가 버튼의 액션 리스너 수정
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String name = nameInput.getText();
				String type = (String) typeInput.getSelectedItem();
				String amount = amountInput.getText();
				String note = noteInput.getText();

				// 날짜와 시간 가져오기
				java.util.Date selectedDate = (java.util.Date) dateInput.getValue();
				java.util.Date selectedTime = (java.util.Date) timeInput.getValue();

				LocalDate localDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

				LocalTime localTime = selectedTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

				// 날짜 + 시간 합치기
				LocalDateTime dateTime = LocalDateTime.of(localDate, localTime);
				Timestamp timestamp = Timestamp.valueOf(dateTime);

				// DB 연결 및 쿼리 실행
				try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root",
						"1234");
						PreparedStatement ps = conn.prepareStatement(
								"INSERT INTO expenses (user_id, category, amount, description, date) VALUES (?, ?, ?, ?, ?)")) {

					ps.setInt(1, loggedInUserId); // user_id
					ps.setString(2, type); // category
					ps.setBigDecimal(3, new BigDecimal(amount)); // amount
					ps.setString(4, note); // description
					ps.setTimestamp(5, timestamp); // 사용자가 선택한 날짜+시간

					ps.executeUpdate();
					JOptionPane.showMessageDialog(null, "거래가 추가되었습니다.");
					td.updateList();
					td.fireTableDataChanged();
					updateNetIncome(summaryLabel);
				} catch (SQLException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "거래 추가 실패: " + e.getMessage());
				}
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 33));
		btnNewButton.setBounds(1073, 495, 291, 71);
		tranPanel.add(btnNewButton);
	}

	// 패스워드 해싱 메서드
	private String hashPassword(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (byte b : hashedBytes) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 카테고리 로드 메서드
	private void loadExpenseCategories() {
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root", "1234");
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM expense_categories")) {
			while (rs.next()) {
				categoryList.add(rs.getString("name"));
			}
			typeInput.setModel(new DefaultComboBoxModel<>(categoryList.toArray(new String[0])));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void showSignupPanel() {
		JFrame signupFrame = new JFrame("회원가입");
		signupFrame.setSize(400, 300);
		signupFrame.getContentPane().setLayout(null);

		JLabel userLabel = new JLabel("아이디:");
		userLabel.setBounds(50, 50, 80, 25);
		signupFrame.getContentPane().add(userLabel);

		JTextField userText = new JTextField();
		userText.setBounds(150, 50, 160, 25);
		signupFrame.getContentPane().add(userText);

		JLabel passwordLabel = new JLabel("비밀번호:");
		passwordLabel.setBounds(50, 100, 80, 25);
		signupFrame.getContentPane().add(passwordLabel);

		JPasswordField passwordText = new JPasswordField();
		passwordText.setBounds(150, 100, 160, 25);
		signupFrame.getContentPane().add(passwordText);

		JButton signupButton = new JButton("가입하기");
		signupButton.setBounds(150, 150, 100, 30);
		signupFrame.getContentPane().add(signupButton);

		signupButton.addActionListener(e -> {
			String username = userText.getText();
			String password = new String(passwordText.getPassword());
			String hashedPassword = hashPassword(password);

			try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root",
					"1234");
					PreparedStatement ps = conn
							.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
				ps.setString(1, username);
				ps.setString(2, hashedPassword);
				ps.executeUpdate();
				JOptionPane.showMessageDialog(signupFrame, "회원가입 성공!");
				signupFrame.dispose();
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(signupFrame, "회원가입 실패: " + ex.getMessage());
			}
		});

		signupFrame.setVisible(true);
	}

	// 월별 요약 업데이트 메서드
	private void updateMonthlySummary(String type, double amount) {
		String month = java.time.LocalDate.now().getMonth().toString();
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root", "1234");
				PreparedStatement ps = conn.prepareStatement(
						"INSERT INTO monthly_summary (month, category, total_amount) VALUES (?, ?, ?) "
								+ "ON DUPLICATE KEY UPDATE total_amount = total_amount + ?")) {
			ps.setString(1, month);
			ps.setString(2, type);
			ps.setDouble(3, amount);
			ps.setDouble(4, amount);

			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 순수익 계산 메소드
	private void updateNetIncome(JLabel summaryLabel) {
		double income = 0;
		double expense = 0;

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root", "1234");
				PreparedStatement ps = conn.prepareStatement(
						"SELECT " + "  CASE WHEN category = '수입' THEN '수입' ELSE '지출' END AS transaction_type, "
								+ "  SUM(amount) AS total " + "FROM expenses "
								+ "WHERE user_id = ? AND DATE_FORMAT(date, '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m') "
								+ "GROUP BY transaction_type")) {

			ps.setInt(1, loggedInUserId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String type = rs.getString("transaction_type");
				double total = rs.getDouble("total");

				if ("수입".equals(type)) {
					income = total;
				} else {
					expense = total;
				}
			}

			double net = income - expense;
			// String result = (net >= 0) ? "흑자" : "적자";
			summaryLabel.setText("이번 달 순수익: " + String.format("%,.0f", net) + "원");

		} catch (SQLException e) {
			e.printStackTrace();
			summaryLabel.setText("순수익 계산 실패: " + e.getMessage());
		}
	}

	// 파이, 그래프 차트 보여줌
	private void showPieAndBarChart() {
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		DefaultCategoryDataset barDataset = new DefaultCategoryDataset();

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root", "1234");
				PreparedStatement ps = conn.prepareStatement("SELECT " + "  CASE "
						+ "    WHEN category IS NULL OR category IN ('Other', '기타', '') THEN '기타' "
						+ "    ELSE category " + "  END AS unified_category, " + "  SUM(amount) AS total "
						+ "FROM expenses " + "WHERE user_id = ? " + "GROUP BY unified_category")) {

			ps.setInt(1, loggedInUserId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String category = rs.getString("unified_category");
				if ("수입".equals(category))
					continue;
				if (category == null || category.trim().isEmpty()) {
					category = "기타";
				}
				double total = rs.getDouble("total");
				pieDataset.setValue(category, total);
				barDataset.addValue(total, "지출", category);
			}

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "차트 로딩 실패: " + e.getMessage());
			return;
		}

		// Pie Chart 생성
		JFreeChart pieChart = ChartFactory.createPieChart("카테고리별 지출 (원형)", pieDataset, true, true, false);
		PiePlot piePlot = (PiePlot) pieChart.getPlot();
		pieChart.getTitle().setFont(new Font("Malgun Gothic", Font.BOLD, 18)); // 제목
		pieChart.getLegend().setItemFont(new Font("Malgun Gothic", Font.PLAIN, 13)); // 범례
		piePlot.setLabelFont(new Font("Malgun Gothic", Font.PLAIN, 14)); // 항목 라벨
		piePlot.setBackgroundPaint(Color.WHITE);
		piePlot.setOutlineVisible(false);
		piePlot.setShadowPaint(null);
		piePlot.setLabelFont(new Font("Malgun Gothic", Font.PLAIN, 14));
		piePlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1}원 ({2})",
				NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()));
		Color[] pastelColors = { new Color(135, 206, 250), // 하늘색
				new Color(144, 238, 144), // 민트
				new Color(255, 182, 193), // 핑크
				new Color(255, 228, 102), // 노랑
				new Color(204, 153, 255), // 보라
				new Color(255, 204, 153), // 오렌지
				new Color(153, 204, 255) }; // 블루
		List<?> keys = pieDataset.getKeys();
		for (int i = 0; i < keys.size(); i++) {
			Object key = keys.get(i);
			piePlot.setSectionPaint((Comparable<?>) key, pastelColors[i % pastelColors.length]);
		}

		// Bar Chart 생성
		JFreeChart barChart = ChartFactory.createBarChart("카테고리별 지출 (막대)", "카테고리", "금액 (원)", barDataset);
		// 사용자 정의 색상
		Map<String, Color> categoryColorMap = new HashMap<>();
		categoryColorMap.put("식비", new Color(144, 238, 144)); // 민트
		categoryColorMap.put("쇼핑", new Color(255, 182, 193)); // 핑크
		categoryColorMap.put("여가", new Color(255, 228, 102)); // 노랑
		categoryColorMap.put("기타", new Color(135, 206, 250)); // 하늘색
		categoryColorMap.put("교통", new Color(204, 153, 255)); // 보라
		categoryColorMap.put("공과금", new Color(255, 204, 153)); // 오렌지

		// 사용자 정의 BarRenderer
		BarRenderer renderer = new BarRenderer() {
			@Override
			public Paint getItemPaint(int row, int column) {
				String category = (String) barDataset.getColumnKey(column);
				return categoryColorMap.getOrDefault(category, Color.GRAY);
			}
		};

		// 차트에 렌더러 적용
		CategoryPlot barPlot = barChart.getCategoryPlot();
		barPlot.setRenderer(renderer);
		renderer.setBarPainter(new StandardBarPainter());
		renderer.setDrawBarOutline(false);
		renderer.setShadowVisible(false);
		barPlot.setBackgroundPaint(Color.WHITE);
		barPlot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		Font font = new Font("Malgun Gothic", Font.PLAIN, 14);
		barChart.getTitle().setFont(new Font("Malgun Gothic", Font.BOLD, 18));
		barChart.getLegend().setItemFont(font);
		barPlot.getDomainAxis().setLabelFont(font);
		barPlot.getDomainAxis().setTickLabelFont(font);
		barPlot.getRangeAxis().setLabelFont(font);

		// 패널 구성
		ChartPanel piePanel = new ChartPanel(pieChart);
		ChartPanel barPanel = new ChartPanel(barChart);
		piePanel.setPreferredSize(new Dimension(500, 300));
		barPanel.setPreferredSize(new Dimension(500, 300));

		JPanel chartContainer = new JPanel();
		chartContainer.setLayout(new GridLayout(2, 1)); // 위아래 배치
		chartContainer.add(piePanel);
		chartContainer.add(barPanel);

		JFrame frame = new JFrame("지출 차트");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(600, 700);
		frame.setLocationRelativeTo(null);
		frame.setContentPane(chartContainer);
		frame.setVisible(true);
	}

	// 월별 비교 그래프
	private void showMonthlyExpenseChart() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root", "1234");
				PreparedStatement ps = conn
						.prepareStatement("SELECT DATE_FORMAT(date, '%Y-%m') AS month, SUM(amount) AS total "
								+ "FROM expenses WHERE user_id = ? AND category != '수입' "
								+ "GROUP BY month ORDER BY month")) {

			ps.setInt(1, loggedInUserId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String month = rs.getString("month");
				double total = rs.getDouble("total");
				dataset.addValue(total, "지출", month);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "월별 지출 차트 로딩 실패: " + e.getMessage());
			return;
		}

		JFreeChart chart = ChartFactory.createBarChart("월별 지출", "월", "금액 (원)", dataset);

		CategoryPlot plot = chart.getCategoryPlot();
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setSeriesPaint(0, new Color(135, 206, 250)); // 하늘색
		renderer.setBarPainter(new StandardBarPainter());
		renderer.setDrawBarOutline(false);
		renderer.setShadowVisible(false);
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

		Font font = new Font("Malgun Gothic", Font.PLAIN, 14);
		chart.getTitle().setFont(new Font("Malgun Gothic", Font.BOLD, 18));
		chart.getLegend().setItemFont(font);
		plot.getDomainAxis().setLabelFont(font);
		plot.getDomainAxis().setTickLabelFont(font);
		plot.getRangeAxis().setLabelFont(font);

		ChartPanel chartPanel = new ChartPanel(chart);
		JFrame frame = new JFrame("월별 지출 차트");
		frame.setSize(700, 500);
		frame.setLocationRelativeTo(null);
		frame.setContentPane(chartPanel);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}

}
