

package com.bestremit.beans;

import java.util.ArrayList;

/**
 * RemitanceProviderBean
 *
 * @author skoppa
 * @version $Id$
 */
public class RemitanceProviderBean {
    
    private String remittanceProviderName;
    
    public String getRemittanceProviderName() {
        return remittanceProviderName;
    }

    public void setRemittanceProviderName(String remittanceProviderName) {
        this.remittanceProviderName = remittanceProviderName;
    }

    private String currencySymbol;
    
    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public ArrayList<RemittanceCurrencyBean> getRemittanceCurrencies() {
        if(remittanceCurrencies == null){
            remittanceCurrencies = new ArrayList<RemittanceCurrencyBean>();
        }
        return remittanceCurrencies;
    }

    public void setRemittanceCurrencies(ArrayList<RemittanceCurrencyBean> remittanceCurrencies) {
        this.remittanceCurrencies = remittanceCurrencies;
    }

    private ArrayList<RemittanceCurrencyBean> remittanceCurrencies;
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("RemintanceBean [remittanceProviderName : ").append(remittanceProviderName);
        sb.append(" currencySymbol : ").append(currencySymbol);
        sb.append("remittanceCurrencies : {").append(remittanceCurrencies);
        sb.append("}] \n");
        
         return sb.toString();
    }

}
