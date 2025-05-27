import javax.swing.table.AbstractTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableData extends AbstractTableModel {
	private List<Transaction> list;
	private String[] headers = {"Name", "Category", "Amount", "Description", "Date"};
	private int userId; // 현재 로그인된 사용자 ID

	public TableData(int userId) {
		this.userId = userId;
		updateList();
	}

	public void setUserId(int userId) {
		this.userId = userId;
		refresh();  // 유저가 바뀌면 바로 데이터 새로고침
	}

	@Override
	public String getColumnName(int cell) {
		return headers[cell];
	}

	// userId에 해당하는 데이터만 가져오도록 수정
	public void updateList() {
		list = new ArrayList<>();
		try (Connection conn = DatabaseConnection.connect()) {
			// category_id와 category_name 둘 다 SELECT함
			String query = "SELECT e.amount, e.description, e.date, u.username, c.id AS category_id, c.name AS category_name " +
					"FROM expenses e " +
					"JOIN users u ON e.user_id = u.id " +
					"JOIN expense_categories c ON e.category_id = c.id " +
					"WHERE e.user_id = ?";

			try (PreparedStatement pstmt = conn.prepareStatement(query)) {
				pstmt.setInt(1, userId);  // 현재 사용자 ID 설정
				try (ResultSet rs = pstmt.executeQuery()) {
					while (rs.next()) {
						int categoryId = rs.getInt("category_id");
						String categoryName = rs.getString("category_name"); // 카테고리 이름
						double amount = rs.getDouble("amount");
						String description = rs.getString("description");
						Date date = rs.getDate("date");
						String username = rs.getString("username");

						// Transaction 객체 생성 및 설정
						Transaction t = new Transaction();
						t.setName(username);            // 사용자 이름
						t.setCategoryId(categoryId);    // category_id
						t.setType(categoryName);        // 카테고리 이름 저장!
						t.setAmount(amount);            // 금액
						t.setNote(description);         // 설명
						t.setDate(date);                // 날짜

						// 리스트에 추가
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
			case 0: return t.getName();
			case 1: return t.getType();  // category_name 대신 t.getType()을 반환하도록 수정
			case 2: return t.getAmount();
			case 3: return t.getNote();
			case 4: return t.getDate();
			default: return null;
		}
	}

	public void refresh() {
		updateList();
		fireTableDataChanged();
	}
}
