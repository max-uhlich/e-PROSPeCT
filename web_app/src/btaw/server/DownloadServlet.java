package btaw.server;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DownloadServlet extends HttpServlet {
	
	private static final long serialVersionUID = -5224217401996457057L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
        String result = req.getParameter("fileInfo1");
        
        Path path = Paths.get(result);
        String fileName = path.getFileName().toString();	

        int BUFFER = 1024*100;
        //resp.setContentType( "application/octet-stream" );
        resp.setContentType("text/csv");
        //resp.setHeader("Content-Disposition:", "attachment;filename=" + "\"" + fileName + "\"" );
        resp.setHeader("Content-Disposition", "filename=" + fileName);
        ServletOutputStream outputStream = resp.getOutputStream();
        //resp.setContentLength(Long.valueOf(getfile(fileName).length()).intValue());
        resp.setBufferSize(BUFFER);
        //Your IO code goes here to create a file and set to outputStream//

        //Path path = Paths.get("/opt/apache-tomcat-8.0.33/webapps/ROOT/resources/runtime/" + fileName);
        //Path path = Paths.get("C:\\Users\\uhlich\\workspace\\btaw\\war\\resources\\runtime\\" + fileName);
        byte[] data = Files.readAllBytes(path);
        resp.setContentLength(Long.valueOf(data.length).intValue());
        
        ServletOutputStream out = resp.getOutputStream();
        out.write(data);
        out.flush();
    }
	
}