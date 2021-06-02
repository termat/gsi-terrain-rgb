package net.termat.dem;

import static spark.Spark.get;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;

import spark.Spark;

public class Main {

	public static void main(String[] args) {
		Optional<String> optionalPort = Optional.ofNullable(System.getenv("PORT"));
		optionalPort.ifPresent( p -> {
			int port = Integer.parseInt(p);
			Spark.port(port);
		});
		Spark.staticFileLocation("/public");
		String path=System.getProperty("user.dir");
		TileDB tileDB=new TileDB();
		try{
			tileDB.connectDB(path+"/tile.db",true);
		}catch(Exception e){}
		new CorsFilter().apply();
		get("/dem/:z/:x/:y", (request, response) -> {
	    	try{
				Integer z=Integer.parseInt(request.params("z"));
				if(z>15)return null;
				Integer x=Integer.parseInt(request.params("x"));
				Integer y=Integer.parseInt(request.params("y"));
	    		Tile t=tileDB.getTile(z, x, y);
	    		if(t==null){
	    			t=createTile(z,x,y);
	    			tileDB.addTile(t);
	    		}
				if(t!=null&&t.imageBytes!=null){
					response.status(200);
					response.header("Content-Type", "image/png");
					return t.imageBytes;
				}else{
					response.status(400);
					return null;
				}
	    	}catch(Exception e){
	        	response.status(400);
	    		return null;
	    	}
		});
	}

	private static Tile createTile(int z,int x,int y) throws MalformedURLException, IOException{
		Tile t=TileUtil.getTile(z, x, y);
		return t;
	}
}
