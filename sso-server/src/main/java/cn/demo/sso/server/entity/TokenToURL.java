package cn.demo.sso.server.entity;

import java.util.ArrayList;
import java.util.List;

public class TokenToURL {
    private String token;
    
    private String username;
    
    private List<String> logoutURLs = new ArrayList<>();
    private List<String> jsessionIds = new ArrayList<>();

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<String> getLogoutURLs() {
        return logoutURLs;
    }

    public void setLogoutURLs(List<String> logoutURLs) {
        this.logoutURLs = logoutURLs;
    }

    public void addLogoutURL(String logoutURL) {
        this.logoutURLs.add(logoutURL);
    }
    
    public void addJsessionId(String jsessionIds) {
        this.jsessionIds.add(jsessionIds);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

	public List<String> getJsessionids() {
		return jsessionIds;
	}

	public void setJsessionids(List<String> jsessionids) {
		this.jsessionIds = jsessionids;
	}

	@Override
	public String toString() {
		return "TokenToURL [token=" + token + ", username=" + username + ", logoutURLs=" + logoutURLs + "]";
	}
}
