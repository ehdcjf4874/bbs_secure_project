package jdbc;

import java.sql.*;
public class Jdbc {
	
	public static void main(String[] args){
		String url = "jdbc:mysql://127.0.0.1/bbs?userSSL=false&user=root&password=1111";
		Connection conn =null;
		
	    Statement stmt = null; //Statement 객체 선언
	    ResultSet rs = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("드라이버 로드 성공");
			
			conn = DriverManager.getConnection(url);
			System.out.println("데이터베이스 접속 성공");
			
			stmt = conn.createStatement(); //쿼리 준비 단계
			String sql="select userId from user"; //전송할 쿼리문           
			//stmt.executeQuery(sql); //쿼리문 전송
			rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				System.out.println
				(
						rs.getString(1) //본문 설명
						
						
				);
			}
		
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		} catch(SQLException se) {
			se.printStackTrace();
		} finally {
			if(conn!=null) try {conn.close();} catch (SQLException e) {}
		}
	}
}
