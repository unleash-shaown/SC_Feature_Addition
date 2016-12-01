/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progoti.feature;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Shaown
 */
public class Response {
    private Map<String, InnerStatus> innerStatusMap = new HashMap<>();

    public Response() {
        innerStatusMap.put("RBL", new InnerStatus("RBL"));
        innerStatusMap.put("FSIBL", new InnerStatus("FSIBL"));
        innerStatusMap.put("BCBL", new InnerStatus("BCBL"));
        innerStatusMap.put("JBL", new InnerStatus("JBL"));
    }

    public Map<String, InnerStatus> getInnerStatusMap() {
        return innerStatusMap;
    }

    public void setInnerStatusMap(Map<String, InnerStatus> innerStatusMap) {
        this.innerStatusMap = innerStatusMap;
    }

    class InnerStatus{
        private String bankName;
        private String status = DBOperation.Status.FAILED.getValue();

        public InnerStatus(String bankName) {
            this.bankName = bankName;
        }

        public String getBankName() {
            return bankName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    @Override
    public String toString() {
        String response = "";
        for(String key : this.innerStatusMap.keySet()){
            response += "Bank: " + this.innerStatusMap.get(key).getBankName() + " Status: " + this.innerStatusMap.get(key).getStatus() + "\n";
        }
        return response;
    }
}
