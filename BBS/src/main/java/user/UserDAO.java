package user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserDAO {
	
	private Connection conn; //자바와 데이터베이스를 연결
	private PreparedStatement pstmt; //쿼리문 대기 및 설정
	private ResultSet rs; //결과값 받아오기
	
	//기본 생성자
	//UserDAO가 실행되면 자동으로 생성되는 부분
	//메소드마다 반복되는 코드를 이곳에 넣으면 코드가 간소화된다.
	public UserDAO() {
		try {
			String dbURL="jdbc:mysql://localhost:3306/bbs";
			String dbID="root";
			String dbPassword="1111";
			Class.forName("com.mysql.cj.jdbc.Driver"); 
			conn = DriverManager.getConnection(dbURL, dbID, dbPassword);
			}
		catch( Exception e) {
			e.printStackTrace();
		}
	}
	
	//id와 password의 최대 길이제한을 8character와 16character로 제한한다.
	private final static int MAX_USER_ID_LENGTH=8;
	private final static int MAX_PASSWORD_LENGTH=16;
	
	
	
	//select, delete, update, insert 등 기존 명령어와 알파벳, 숫자를 제외한 다른 문자들
	//인젝션에 사용되는 특수문자 포함)을 검출하는 정규식을 설정한다.
	private final static String UNSECURED_CHAR_REGULAR_EXPRESSION =
			"[^\\p{Alnum}]|select|delete|update|insert|create|alter|drop";
	
	private Pattern unsecuredCharPattern;
	
	// 정규식을 초기화 한다.
	public void initialize()
	{
		unsecuredCharPattern =	Pattern.compile(UNSECURED_CHAR_REGULAR_EXPRESSION ,Pattern.CASE_INSENSITIVE);
	}
	
	//입력값을 정규식을 이용해 필터링 한 후 의심되는 부분을 없앤다.
	//substring하는 부분에서 오류
	private String makeSecureString(final String str, int maxLength)
	{
		String secureStr = str.substring(0, maxLength);
		
		Matcher matcher = unsecuredCharPattern.matcher(secureStr);
		
		return matcher.replaceAll("");
				}			
	//로그인 영역
	public int login(String userID, String userPassword) {
		
		String sql;
		//아이디와 패스워드 최대길이 제한
			sql ="select"+makeSecureString(userPassword,MAX_PASSWORD_LENGTH)+"from user where"+makeSecureString(userID,MAX_USER_ID_LENGTH)+"=?";
		try {
			pstmt=conn.prepareStatement(sql); //sql 쿼리문을 대기 시킨다.
			pstmt.setString(1, userID); // 첫번째 '?'에 매개변수로 받아온 'userId'를 대입
			rs=pstmt.executeQuery();
			if(rs.next()) {
				if(rs.getString(1).equals(userPassword)) 
					return 1; //로그인 성공
			else
					return 0; //비밀번호 틀림
		}
		return -1; //아이디 없음
	} catch(Exception e) {
		e.printStackTrace();
	}
	return -2; //오류
	}
	
	public int join(User user) {
		String sql = "insert into user values(?,?,?,?,?)";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1,  user.getUserID());
			pstmt.setString(2,  user.getUserPassword());
			pstmt.setString(3,  user.getUserName());
			pstmt.setString(4,  user.getUserGender());
			pstmt.setString(5,  user.getUserEmail());
			return pstmt.executeUpdate();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
}
