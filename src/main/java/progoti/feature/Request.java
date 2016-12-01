/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progoti.feature;

/**
 *
 * @author Shaown
 */
public class Request {
    private String controller;
    private String action;
    private String title;
    private String role;
    private String db;

    public Request(String controller, String action, String title, String role, String db) {
        this.controller = controller;
        this.action = action;
        this.title = title;
        this.role = role;
        this.db = db;
    }
    
    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }
}
