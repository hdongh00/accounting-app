import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.Font;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Collections;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;




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
	private int loggedInUserId;  // 로그인한 사용자의 ID

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
		ImagePanel loginPanel = new ImagePanel(new ImageIcon("C:/Users/hdong/eclipse-workspace/AccountingApp/Image Sources/theme.jpg").getImage());

		currPanel = loginPanel;
		ImagePanel tranPanel = new ImagePanel(new ImageIcon("C:/Users/hdong/eclipse-workspace/AccountingApp/Image Sources/Activation.jpg").getImage());

		frame.setSize(loginPanel.getDim());
		frame.setPreferredSize(loginPanel.getDim());
		ImagePanel sumPanel = new ImagePanel(new ImageIcon("C:/Users/hdong/eclipse-workspace/AccountingApp/Image Sources/Activation.jpg").getImage());
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
		tranBtn.setBounds(27, 191, 246, 40);//원래 setbound(29,182,259,40)
		tranBtn.setBorder(null);
		sumPanel.add(tranBtn);

		JButton chartBtn = new JButton("Show Chart");
		chartBtn.setBounds(29, 252, 228, 40);
		chartBtn.setBackground(Color.BLACK); // 배경색 검정
		chartBtn.setForeground(Color.WHITE); // 글자색 흰색
		chartBtn.setFocusPainted(false);     // 포커스 테두리 제거
		chartBtn.setBorderPainted(false);    // 외곽선 제거
		chartBtn.setFont(new Font("Arial", Font.BOLD, 14)); // 글꼴

// 마우스 오버 시 색상 변화
		chartBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				chartBtn.setBackground(Color.DARK_GRAY);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				chartBtn.setBackground(Color.BLACK);
			}
		});

		sumPanel.add(chartBtn);


		chartBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showPieAndBarChart();
			}
		});

		JButton monthlyExpenseBtn = new JButton("Show MonthChart");
		monthlyExpenseBtn.setBounds(29, 300, 228, 40);
		monthlyExpenseBtn.setBackground(Color.BLACK); // 배경색 검정
		monthlyExpenseBtn.setForeground(Color.WHITE); // 글자색 흰색
		monthlyExpenseBtn.setFocusPainted(false);     // 포커스 테두리 제거
		monthlyExpenseBtn.setBorderPainted(false);    // 외곽선 제거
		monthlyExpenseBtn.setFont(new Font("Arial", Font.BOLD, 14)); // 글꼴

// 마우스 오버 시 색상 변화
		monthlyExpenseBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				monthlyExpenseBtn.setBackground(Color.DARK_GRAY);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				monthlyExpenseBtn.setBackground(Color.BLACK);
			}
		});

		sumPanel.add(monthlyExpenseBtn);

		monthlyExpenseBtn.addActionListener(e -> showMonthlyExpenseChart());

		JButton reportBtn = new JButton("보고서 생성");
		reportBtn.setBounds(29, 348, 228, 40);
		reportBtn.setBackground(Color.LIGHT_GRAY);
		reportBtn.setForeground(Color.BLACK);
		reportBtn.setFocusPainted(false);
		reportBtn.setBorderPainted(false);
		reportBtn.setFont(new Font("Malgun gothic", Font.BOLD, 14));
		reportBtn.addActionListener(e -> {
			generateTransactionReportPDF(table);  // table은 거래내역 JTable
		});
		sumPanel.add(reportBtn);

		JLabel lblSearch = new JLabel("Search :");
		lblSearch.setFont(new Font("Tahoma", Font.PLAIN, 22));
		lblSearch.setBounds(317, 75, 83, 40);//원래(337,76,83,40)
		sumPanel.add(lblSearch);

		searchInput = new JTextField();
		searchInput.setFont(new Font("Malgun Gothic", Font.PLAIN, 22));
		searchInput.setBounds(413, 76, 979, 40);//(432,76,1080,40)
		sumPanel.add(searchInput);
		searchInput.setColumns(10);

		table = new JTable(td);
		table.setBounds(337, 140, 1155, 445);
		table.setRowHeight(30);
		table.setFont(new Font("Sansserif", Font.BOLD, 15));
		table.setPreferredScrollableViewportSize(new Dimension(1155, 430));
		//거래 내역 표 보여주는 코드
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(302, 138, 1090, 432);
		sumPanel.add(scrollPane);
		sumPanel.setLayout(null);


		JPanel tp = new JPanel();
		tp.setBounds(293, 138, 10, 430);//(337,140,1175,467)
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

		frame.getContentPane().add(tranPanel);

		tranPanel.setVisible(false);
		frame.getContentPane().add(loginPanel);

		idField = new JTextField();
		idField.setFont(new Font("Tahoma", Font.PLAIN, 26));
		idField.setBounds(1118, 325, 264, 43);//(1223,311,296,43)
		loginPanel.add(idField);
		idField.setColumns(10);
		idField.setBorder(null);

		passField = new JPasswordField();
		passField.setFont(new Font("Tahoma", Font.PLAIN, 26));
		passField.setBounds(1118, 408, 264, 43);//(1223,391,296,43)
		passField.setBorder(null);
		loginPanel.add(passField);

		JCheckBox chckbxNewCheckBox = new JCheckBox("");
		chckbxNewCheckBox.setBounds(1081, 461, 25, 25);//(1184,440,25,25)
		loginPanel.add(chckbxNewCheckBox);

		JButton logInBtn = new JButton("");
		logInBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String inputId = idField.getText();
				String inputPass = new String(passField.getPassword());
				String hashedPass = hashPassword(inputPass);

				try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root", "1234");
					 PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
					ps.setString(1, inputId);
					ps.setString(2, hashedPass);

					//ps.setString(2, hashedPass);, 오류가 나서 일단 inputPass로 바꿈

					ResultSet rs = ps.executeQuery();
					if (rs.next()) {
						loggedInUserId = rs.getInt("id");// userId 저장
						td.setUserId(loggedInUserId);      // 핵심: 로그인된 유저 ID로 교체
						td.refresh();					   // 표가 안떠서 추가한 코드, 주석 처리 가능
						currPanel.setVisible(false);
						sumPanel.setVisible(true);
						currPanel = sumPanel;
						//updateNetIncome(summaryLabel);
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
		logInBtn.setBounds(1081, 488, 301, 38);//(1183,467,338,38)
		loginPanel.add(logInBtn);
		logInBtn.setIcon(new ImageIcon("C:/Users/hdong/eclipse-workspace/AccountingApp/Image Sources/button.jpg"));
		logInBtn.setPressedIcon(new ImageIcon("C:/Users/hdong/eclipse-workspace/AccountingApp/Image Sources/btnClicked.jpg"));

		JButton signupBtn = new JButton("회원가입");
		signupBtn.setBounds(1221, 536, 161, 38); // 위치 조정,(1183,520,338,38)
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
		sumBtn.setBounds(20, 130, 247, 40);//(29,123,259,40)
		tranPanel.add(sumBtn);

		JLabel lblName = new JLabel("Name");
		lblName.setFont(new Font("Tahoma", Font.PLAIN, 33));
		lblName.setBounds(320, 125, 139, 49);//(378,123,139,49)
		tranPanel.add(lblName);

		JLabel lblType = new JLabel("Type");
		lblType.setFont(new Font("Tahoma", Font.PLAIN, 33));
		lblType.setBounds(320, 204, 139, 49);//(378,203,139,49)
		tranPanel.add(lblType);

		JLabel lblAmount = new JLabel("Amount");
		lblAmount.setFont(new Font("Tahoma", Font.PLAIN, 33));
		lblAmount.setBounds(320, 284, 139, 49);//(378,284,139,49)
		tranPanel.add(lblAmount);

		JLabel lblNote = new JLabel("Note");
		lblNote.setFont(new Font("Tahoma", Font.PLAIN, 33));
		lblNote.setBounds(320, 370, 139, 49);//(378,370,139,49)
		tranPanel.add(lblNote);

		nameInput = new JTextField();
		nameInput.setFont(new Font("Malgun Gothic", Font.PLAIN, 33));
		nameInput.setBounds(452, 130, 912, 49);//(527,123,935,49)
		tranPanel.add(nameInput);
		nameInput.setColumns(10);

		typeInput = new JComboBox();
		loadExpenseCategories(); // 카테고리 로딩
		typeInput.setFont(new Font("Malgun Gothic", Font.PLAIN, 33));
		typeInput.setBounds(452, 203, 912, 49);//(527,203,935,49)
		tranPanel.add(typeInput);
		typeInput.setBackground(Color.WHITE);

		amountInput = new JTextField();
		amountInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
		amountInput.setColumns(10);
		amountInput.setBounds(452, 284, 912, 49);//(527,284,935,49)
		tranPanel.add(amountInput);

		JTextArea noteInput = new JTextArea();
		noteInput.setFont(new Font("Malgun Gothic", Font.PLAIN, 33));
		noteInput.setBounds(452, 370, 912, 60);//(527,370,935,60)
		tranPanel.add(noteInput);
		noteInput.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		// 날짜 입력 라벨
		JLabel lblDate = new JLabel("Date");
		lblDate.setFont(new Font("Tahoma", Font.PLAIN, 33));
		lblDate.setBounds(320, 464, 139, 49);//(378,490,139,49)
		tranPanel.add(lblDate);

		// 날짜 선택 스피너
		SpinnerDateModel dateModel = new SpinnerDateModel();
		JSpinner dateInput = new JSpinner(dateModel);
		JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateInput, "yyyy-MM-dd");
		dateInput.setEditor(dateEditor);
		dateInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
		dateInput.setBounds(452, 464, 300, 49);//(527,490,300,49)
		tranPanel.add(dateInput);

		// 시간 입력 라벨
		JLabel lblTime = new JLabel("Time");
		lblTime.setFont(new Font("Tahoma", Font.PLAIN, 33));
		lblTime.setBounds(320, 523, 139, 49);//(378,550,139,49)
		tranPanel.add(lblTime);

		// 시간 선택 스피너
		SpinnerDateModel timeModel = new SpinnerDateModel();
		JSpinner timeInput = new JSpinner(timeModel);
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeInput, "HH:mm:ss");
		timeInput.setEditor(timeEditor);
		timeInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
		timeInput.setBounds(452, 523, 300, 49);//(527,550,300,49)
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
				//날짜 + 시간 합치기
				LocalDateTime dateTime = LocalDateTime.of(localDate, localTime);
				Timestamp timestamp = Timestamp.valueOf(dateTime);

				int categoryId = -1;

				try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root", "1234")) {
					try (PreparedStatement catStmt = conn.prepareStatement("SELECT id FROM expense_categories WHERE name = ?")) {
						catStmt.setString(1, type);
						try (ResultSet catRs = catStmt.executeQuery()) {
							if (catRs.next()) {
								categoryId = catRs.getInt("id");
							} else {
								JOptionPane.showMessageDialog(null, "해당 카테고리가 없습니다.");
								return;
							}
						}
					}

					try (PreparedStatement ps = conn.prepareStatement("INSERT INTO expenses (user_id, category_id, amount, description, date) VALUES (?, ?, ?, ?, ?)")) {
						ps.setInt(1, loggedInUserId);
						ps.setInt(2, categoryId);
						ps.setBigDecimal(3, new BigDecimal(amount));
						ps.setString(4, note);
						ps.setTimestamp(5, timestamp);
						ps.executeUpdate();
						JOptionPane.showMessageDialog(null, "거래가 추가되었습니다.");
						td.updateList();
						td.fireTableDataChanged();
						//updateNetIncome(summaryLabel);
					}
				} catch (SQLException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "DB 오류: " + e.getMessage());
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
		signupFrame.setLayout(null);

		JLabel userLabel = new JLabel("아이디:");
		userLabel.setBounds(50, 50, 80, 25);
		signupFrame.add(userLabel);

		JTextField userText = new JTextField();
		userText.setBounds(150, 50, 160, 25);
		signupFrame.add(userText);

		JLabel passwordLabel = new JLabel("비밀번호:");
		passwordLabel.setBounds(50, 100, 80, 25);
		signupFrame.add(passwordLabel);

		JPasswordField passwordText = new JPasswordField();
		passwordText.setBounds(150, 100, 160, 25);
		signupFrame.add(passwordText);

		JButton signupButton = new JButton("가입하기");
		signupButton.setBounds(150, 150, 100, 30);
		signupFrame.add(signupButton);

		signupButton.addActionListener(e -> {
			String username = userText.getText();
			String password = new String(passwordText.getPassword());
			String hashedPassword = hashPassword(password);

			try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root", "1234");
				 PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
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
					 "INSERT INTO monthly_summary (month, category, total_amount) VALUES (?, ?, ?) " +
							 "ON DUPLICATE KEY UPDATE total_amount = total_amount + ?")) {
			ps.setString(1, month);
			ps.setString(2, type);
			ps.setDouble(3, amount);
			ps.setDouble(4, amount);

			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// 파이, 그래프 차트 보여줌
	private void showPieAndBarChart() {
		JFrame frame = new JFrame("지출 차트");
		frame.setLayout(new BorderLayout());

		JPanel topPanel = new JPanel();
		JLabel label = new JLabel("월 선택: ");
		JComboBox<String> monthComboBox = new JComboBox<>();
		topPanel.add(label);
		topPanel.add(monthComboBox);
		frame.add(topPanel, BorderLayout.NORTH);

		JPanel chartContainer = new JPanel(new GridLayout(2, 1)); // 차트 패널
		frame.add(chartContainer, BorderLayout.CENTER);

		// 콤보박스 갱신용 월 데이터 가져오기
		Set<String> availableMonths = new TreeSet<>(Collections.reverseOrder());
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root", "1234");
			 PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT DATE_FORMAT(e.date, '%Y-%m') AS month " +
					 "FROM expenses e WHERE e.user_id = ?")) {
			ps.setInt(1, loggedInUserId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				availableMonths.add(rs.getString("month"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "월 목록 로딩 실패: " + e.getMessage());
		}

		for (String month : availableMonths) {
			monthComboBox.addItem(month);
		}

		// 실제 차트 표시 메서드
		Runnable drawCharts = () -> {
			chartContainer.removeAll();

			DefaultPieDataset pieDataset = new DefaultPieDataset();
			DefaultCategoryDataset barDataset = new DefaultCategoryDataset();

			try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root", "1234");
				 PreparedStatement ps = conn.prepareStatement(
						 "SELECT " +
								 "CASE WHEN ec.name IS NULL OR ec.name IN ('Other', '기타', '') THEN '기타' ELSE ec.name END AS unified_category, " +
								 "SUM(e.amount) AS total " +
								 "FROM expenses e " +
								 "JOIN expense_categories ec ON e.category_id = ec.id " +
								 "WHERE e.user_id = ? AND DATE_FORMAT(e.date, '%Y-%m') = ? " +
								 "GROUP BY unified_category"
				 )) {

				ps.setInt(1, loggedInUserId);
				ps.setString(2, (String) monthComboBox.getSelectedItem());
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					String category = rs.getString("unified_category");
					if ("수입".equals(category)) continue;
					if (category == null || category.trim().isEmpty()) category = "기타";
					double total = rs.getDouble("total");
					pieDataset.setValue(category, total);
					barDataset.addValue(total, "지출", category);
				}

			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "차트 로딩 실패: " + e.getMessage());
				return;
			}

			Map<String, Color> categoryColorMap = new HashMap<>();
			categoryColorMap.put("식비", new Color(144, 238, 144));
			categoryColorMap.put("쇼핑", new Color(255, 182, 193));
			categoryColorMap.put("여가", new Color(255, 228, 102));
			categoryColorMap.put("기타", new Color(135, 206, 250));
			categoryColorMap.put("교통", new Color(204, 153, 255));
			categoryColorMap.put("공과금", new Color(255, 204, 153));

			// Pie Chart
			JFreeChart pieChart = ChartFactory.createPieChart("카테고리별 지출 (원형)", pieDataset, true, true, false);
			PiePlot piePlot = (PiePlot) pieChart.getPlot();
			pieChart.getTitle().setFont(new Font("Malgun Gothic", Font.BOLD, 18));
			pieChart.getLegend().setItemFont(new Font("Malgun Gothic", Font.PLAIN, 13));
			piePlot.setLabelFont(new Font("Malgun Gothic", Font.PLAIN, 14));
			piePlot.setBackgroundPaint(Color.WHITE);
			piePlot.setOutlineVisible(false);
			piePlot.setShadowPaint(null);
			piePlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));
			for (Object key : pieDataset.getKeys()) {
				Color color = categoryColorMap.getOrDefault(key.toString(), Color.GRAY);
				piePlot.setSectionPaint((Comparable<?>) key, color);
			}

			// Bar Chart
			JFreeChart barChart = ChartFactory.createBarChart("카테고리별 지출 (막대)", "카테고리", "금액 (원)", barDataset);
			CategoryPlot barPlot = barChart.getCategoryPlot();
			BarRenderer renderer = new BarRenderer() {
				@Override
				public Paint getItemPaint(int row, int column) {
					String category = (String) barDataset.getColumnKey(column);
					return categoryColorMap.getOrDefault(category, Color.GRAY);
				}
			};
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
			chartContainer.add(piePanel);
			chartContainer.add(barPanel);

			frame.revalidate();
			frame.repaint();
		};

		// 콤보박스 변경 시 차트 새로 그림
		monthComboBox.addActionListener(e -> drawCharts.run());

		if (monthComboBox.getItemCount() > 0) {
			monthComboBox.setSelectedIndex(0);
			drawCharts.run();
		}

		frame.setSize(600, 700);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}


	// 월별 비교 그래프
	private void showMonthlyExpenseChart() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root", "1234");
			 PreparedStatement ps = conn
					 .prepareStatement("SELECT DATE_FORMAT(e.date, '%Y-%m') AS month, SUM(e.amount) AS total " +
							 "FROM expenses e " +
							 "JOIN expense_categories c ON e.category_id = c.id " +
							 "WHERE e.user_id = ? AND c.name != '수입' " +
							 "GROUP BY month ORDER BY month")) {

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

		DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
		for (int i = 0; i < dataset.getColumnCount(); i++) {
			String month = (String) dataset.getColumnKey(i);
			Number value = dataset.getValue(0, i); // "지출" row
			lineDataset.addValue(value, "추세선", month);
		}

// ▼ 추가: 추세선 렌더러 설정
		LineAndShapeRenderer lineRenderer = new LineAndShapeRenderer();
		lineRenderer.setSeriesPaint(0, Color.RED); // 추세선 색
		lineRenderer.setSeriesStroke(0, new BasicStroke(2.0f)); // 선 굵기
		plot.setDataset(1, lineDataset); // 두 번째 데이터셋으로 설정
		plot.setRenderer(1, lineRenderer); // 두 번째 렌더러로 설정
		plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

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
	private void generateTransactionReportPDF(JTable table) {
		try {
			String filePath = "거래내역_보고서.pdf";
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream("거래내역_보고서.pdf"));
			document.open();

			// 제목
			BaseFont bfKorean = BaseFont.createFont("C:\\Windows\\Fonts\\malgun.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			com.lowagie.text.Font titleFont = new com.lowagie.text.Font(bfKorean, 18, com.lowagie.text.Font.BOLD);
			com.lowagie.text.Font contentFont = new com.lowagie.text.Font(bfKorean, 12, Font.PLAIN);

			Paragraph title = new Paragraph("거래 내역 보고서", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);
			document.add(new Paragraph("생성일: " + java.time.LocalDate.now()));
			document.add(Chunk.NEWLINE);

			// 표 생성
			PdfPTable pdfTable = new PdfPTable(table.getColumnCount());
			pdfTable.setWidthPercentage(100);

			// 헤더 셀 추가
			// (1) 헤더 셀
			for (int i = 0; i < table.getColumnCount(); i++) {
				pdfTable.addCell(new PdfPCell(new Phrase(table.getColumnName(i), contentFont)));
			}

// (2) 데이터 셀
			for (int row = 0; row < table.getRowCount(); row++) {
				for (int col = 0; col < table.getColumnCount(); col++) {
					Object value = table.getValueAt(row, col);
					pdfTable.addCell(new PdfPCell(new Phrase(value != null ? value.toString() : "", contentFont)));
				}
			}


			document.add(pdfTable);
			document.close();

			JOptionPane.showMessageDialog(frame, "PDF 보고서가 생성되었습니다.");
			File pdfFile = new File(filePath);
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(pdfFile); // 기본 PDF 뷰어로 열기
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame, "PDF 생성 중 오류가 발생했습니다.");
		}
	}

}
