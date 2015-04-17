import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class DotabuffConnector {
	private String[] heroNamesArray = new String[110];
	
    public DotabuffConnector() {
    	String toParse = "";
    	ArrayList<String> heroNames = new ArrayList<String>();
    	
        try ( Socket outgoing = new Socket("dotabuff.com", 80) ) {
            InputStream inStream = outgoing.getInputStream();
            OutputStream outStream = outgoing.getOutputStream();
            
            Scanner in = new Scanner(inStream);
            PrintWriter out = new PrintWriter(outStream, true);
            
            out.println("GET /heroes/winning HTTP/1.1\n"
            		+ "Host: www.dotabuff.com\n"
            		+ "User-Agent: Mozilla/5.0 (X11; U; Linux i686; ru; rv:1.9b5) Gecko/2008050509 Firefox/3.0b5\n"
            		+ "Accept: text/html\n"
            		+ "Connection: close\n"
            		+ "\n");
            
            //Выделяем из ёбаной html-страницы одну строку со всей ебучей таблицей героев
            while ( in.hasNextLine() ) {
                String line;
                line = in.nextLine();
                
                if ( line.contains("<option value=\"\">All Time</option></select></div></dd></dl></div></form></div></footer><article>") ) {
                	toParse = line;
                }
            }
            
            //Разбиваем это говно на строки и из каждой строки вынимаем имя героя
            String[] stringsToParse = toParse.split("img alt");
            for ( String s : stringsToParse ) {
            	s = s.substring(2, s.length());
            	
            	char c = s.charAt(0);
            	int i = 0;
            	for ( ; c != '\"'; i++ ) {
            		c = s.charAt(i);
            	}
            	heroNames.add(s.substring(0, i-1));
            }
            
            int i = 0;
            ListIterator<String> it = heroNames.listIterator();
            for ( it.next(); it.hasNext(); i++) {
            	heroNamesArray[i] = (String) it.next();
            }
            
            
//            Достаем из каждой строки этого говна все числа
//            for ( String s : stringsToParse ) {
//	            Pattern pat=Pattern.compile("[-]?[0-9]+(.[0-9]+)?");
//	            Matcher matcher=pat.matcher(s);
//	            while (matcher.find()) {
//	                System.out.println(matcher.group());
//	            };
//            }
            
            
            
            System.out.println("Number of heroes: " + heroNames.size());
            System.out.println("Heroes array: " + heroNamesArray.length);
            in.close();
        } catch ( IOException e ) {
            System.err.println("Unable to create socket!");
        }
    }

    public String[] getHeroNames() {
    	return heroNamesArray;
    }
}
