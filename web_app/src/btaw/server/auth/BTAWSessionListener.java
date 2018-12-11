package btaw.server.auth;

import btaw.server.model.db.Database;
import btaw.shared.model.BTAWDatabaseException;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class BTAWSessionListener implements HttpSessionListener {
	
	public BTAWSessionListener() {
		// Nothing to see here.
	}
	@Override
	public void sessionCreated(HttpSessionEvent e) {
		// Anything we might want to do when sessions are created.
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent e) {
		// Any state we have which is not directly bound to the session
		// must be handled here.
		Database db = null;
		try {
			db = Database.getInstance(e.getSession().getServletContext());
			e.getSession().setAttribute("auth-token", null);
		} catch (BTAWDatabaseException ex) {
			// We want to know if this happens.  It shouldn't.
			ex.printStackTrace();
		}
		if (db != null) {
			db.notifySessionDestroyed(e.getSession());
		}
		
	}
}
