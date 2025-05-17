import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class TableData extends AbstractTableModel {
	private List<Transaction> list;
	private String[] headers = { "Name", "Category", "Amount", "Description", "Date" };
	private int userId; // 현재 로그인된 사용자 ID

	public TableData(int userId) {
		this.userId = userId;
		updateList();
	}

	public void setUserId(int userId) {
		this.userId = userId;
		refresh(); // 유저가 바뀌면 바로 데이터 새로고침
	}

	@Override
	public String getColumnName(int cell) {
		return headers[cell];
	}

	// userId에 해당하는 데이터만 가져오도록 수정
	public void updateList() {
		list = new ArrayList<>();
		try (Connection conn = DatabaseConnection.connect()) {
			String query = "SELECT e.category, e.amount, e.description, e.date, u.username " + "FROM expenses e "
					+ "JOIN users u ON e.user_id = u.id " + "WHERE e.user_id = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(query)) {
				pstmt.setInt(1, userId);
				try (ResultSet rs = pstmt.executeQuery()) {
					while (rs.next()) {
						String category = rs.getString("category");
						double amount = rs.getDouble("amount");
						String description = rs.getString("description");
						Date date = rs.getDate("date");
						String username = rs.getString("username");

						Transaction t = new Transaction();
						t.setName(username); // 사용자 이름 설정
						t.setType(category); // 카테고리 설정
						t.setAmount(amount); // 금액 설정
						t.setNote(description); // 설명 설정
						t.setDate(date); // 날짜 설정

						list.add(t);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		return list.size();
	}

	@Override
	public Object getValueAt(int row, int cell) {
		Transaction t = list.get(row);
		switch (cell) {
		case 0:
			return t.getName();
		case 1:
			return t.getType();
		case 2:
			return t.getAmount();
		case 3:
			return t.getNote();
		case 4:
			return t.getDate();
		default:
			return null;
		}
	}

	public void refresh() {
		updateList();
		fireTableDataChanged();
	}
}
