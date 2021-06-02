package net.termat.dem;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;


public class TileDB {
	private ConnectionSource connectionSource = null;
	private Dao<Tile,Long> tileDao;

	/**
	 * データベース接続
	 *
	 * @param dbName DBのパス
	 * @param create DBの自動生成の有無
	 * @throws SQLException
	 */
	public void connectDB(String dbName,boolean create) throws SQLException{
		try{
			if(!dbName.endsWith(".db"))dbName=dbName+".db";
			Class.forName("org.sqlite.JDBC");
			connectionSource = new JdbcConnectionSource("jdbc:sqlite:"+dbName);
			tileDao= DaoManager.createDao(connectionSource, Tile.class);
			if(create)TableUtils.createTableIfNotExists(connectionSource, Tile.class);
			DriverManager.getConnection("jdbc:sqlite:"+dbName);
		}catch(SQLException e){
			e.printStackTrace();
			throw e;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public Tile getTile(int z,int x,int y) throws SQLException{
		QueryBuilder<Tile,Long> query=tileDao.queryBuilder();
		query.where().eq("z", z).and().eq("x", x).and().eq("y",y);
		List<Tile> list=tileDao.query(query.prepare());
		if(list.size()==0){
			return null;
		}else{
			return list.get(0);
		}
	}

	public void addTile(Tile t) throws SQLException{
		tileDao.createIfNotExists(t);
	}


	public void close()throws IOException{
		connectionSource.close();
	}

}
