package PengWin_Package;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

import game.Board;

public class Bot_01 {

	public static final char BLACK = 'b';
	public static final char WHITE = 'w';
	public static final char EMPTY = '+';
	
	public int depth = 6;
	
	public LastMove lastMove = new LastMove();
	
	public Fetch fetchBody = null;
	
	private byte[] blackBuffer = new byte[8];
	private byte[] whiteBuffer = new byte[8];
    
	private byte turn = 1; // 1 for black and 0 for white
	
	private byte[] payload = new byte[17];
	private String urlstr = "http://10.0.0.10:8000/stuff";
	private URL url;
    
    
    public int[] botMove(Board b, char color) {
    	Fetch fetch = fetchEngineMove(b);
    	
    	if(fetch.ok()) {
    		this.parseJson(fetch.response.toString());
    	} else
    		this.fetchBody = fetch;
    	
    	return new int[] {lastMove.x, lastMove.y};
    }
    
    private void setBitboardStruct(Board b) {
    	this.turn = (byte) (b.whoseTurn() == Bot_01.BLACK ? 1 : 0);
    	
    	for(int row = 7; row >= 0; row--) {
    		byte rb = 0, rw = 0;
    		
    		for(int col = 7; col >= 0; col--) {
    			char sq = b.getBoard(row, col);
    			
    			if(sq == Bot_01.EMPTY) continue;
    			if(sq == Bot_01.BLACK) rb |= (byte) 1 << (7 - col);
    			else rw |= (byte) 1 << (7 - col);
    		}
    		
    		this.blackBuffer[7 - row] = rb;
    		this.whiteBuffer[7 - row] = rw;
    	}
    	
    	System.arraycopy(this.blackBuffer, 0, this.payload, 0, 8);
        System.arraycopy(this.whiteBuffer, 0, this.payload, 8, 8);
        this.payload[16] = this.turn;
    }
    
    private Fetch fetchEngineMove(Board b) {
		this.setBitboardStruct(b);
		
		Fetch fetch = new Fetch();
    	
		HttpURLConnection con;
    	try {
			this.url = new URL(urlstr);
			con = (HttpURLConnection) this.url.openConnection();

	        con.setRequestMethod("POST");
		} catch (IOException e) {
			fetch.e = e;
			return fetch;
		}
    	
        con.setRequestProperty("Content-Type", "application/octet-stream");
        con.setDoOutput(true);
        
        try (OutputStream os = con.getOutputStream()) {
            os.write(payload);
            os.flush();
        } catch (IOException e) {
        	fetch.e = e;
			return fetch;
		}
        
        int statusCode = -1;
        try {
			statusCode = con.getResponseCode();
		} catch (IOException e) {
			fetch.statusCode = statusCode;
			return fetch;
		}
        
        try (InputStream is = con.getInputStream()) {
            byte[] resp = is.readAllBytes();
            fetch.response = resp;
        } catch (IOException e) {
			fetch.response = null;
		}
        
        return fetch;
    }
    
    private void parseJson(String json) {
    	json = json.replace("{", "").replace("}", "").replace("\"", "");
    	String[] parts = json.split(",");
    	
    	for(String part : parts) {
    		String[] kv = part.split(":");
    		String key = kv[0].trim();
    		int value = Integer.parseInt(kv[1].trim());
    		
    		switch (key) {
				case "x": lastMove.x = value; break;
				case "y": lastMove.y = value; break;
				case "score": lastMove.score = value; break;
				default: break;
			}
    	}
    }
    
    public class Fetch {
    	public IOException e = null;
    	public int statusCode = -1;
    	public byte[] response = null;
    	
    	boolean ok() {
    		return e == null && statusCode == 200 && response != null;
    	}
    }
    
    public class LastMove {
    	int x = -1;
    	int y = -1;
    	int score = 0;
    	
    	boolean ok() {
    		return x != -1 && y != -1;
    	}
    }
}
